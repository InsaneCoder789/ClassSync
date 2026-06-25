package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.classroom.ClassroomScreenEvent
import com.rochiee.classsync.bloc.classroom.ClassroomScreenState
import com.rochiee.classsync.domain.model.ClassroomDaySchedule
import com.rochiee.classsync.domain.model.ClassroomScheduleEntry
import com.rochiee.classsync.domain.model.ClassroomSection
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.ErrorState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.LoadingState
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.AlertOrange
import com.rochiee.classsync.ui.theme.Ink
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.NightAccent
import com.rochiee.classsync.ui.theme.SkyBlue

private val timetableShell = Color(0xFF121014)
private val timetableShellBorder = Color(0xFF2C2331)
private val timetableCard = Color(0xFF342737)
private val timetableCardHighlight = Color(0xFF1D3359)
private val timetableDayIdle = Color(0xFF262128)
private val timetableDaySelected = SkyBlue
private val timetableAccent = NightAccent
private val timetableTextPrimary = Color(0xFFF8F2F6)
private val timetableTextMuted = Color(0xFFC9BEC7)

@Composable
fun ClassroomScreen(
    classroomState: ClassroomScreenState,
    onClassroomEvent: (ClassroomScreenEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    val semesterOptions = remember { (1..8).toList() }
    val selectedSemester = classroomState.selectedSemester
    val selectedSemesterData = classroomState.catalog.semesters.firstOrNull { it.semesterNumber == selectedSemester }
    val selectedSection = classroomState.selectedSection
    var selectedDayIndex by remember(selectedSection?.sectionId) { mutableIntStateOf(0) }
    val selectedDay = selectedSection?.days?.getOrNull(selectedDayIndex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(spacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(
                text = if (selectedSection == null) "Timetable" else selectedSection.sectionId,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when {
                    selectedSection != null -> "Semester ${selectedSemester ?: "-"} timetable"
                    selectedSemester != null -> "Choose a section for Semester $selectedSemester"
                    else -> "Semester Selection → Section Selection → Timetable"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        when {
            classroomState.isLoading -> LoadingState("Loading classroom catalog...")
            classroomState.errorMessage != null -> ErrorState(classroomState.errorMessage)
            selectedSemester == null -> SemesterSelectionStep(
                semesterOptions = semesterOptions,
                catalogState = classroomState,
                onSelectSemester = { onClassroomEvent(ClassroomScreenEvent.SelectSemester(it)) }
            )
            selectedSection == null -> SectionSelectionStep(
                semester = selectedSemester,
                state = classroomState,
                onBack = { onClassroomEvent(ClassroomScreenEvent.BackToSemesters) },
                onRefresh = { onClassroomEvent(ClassroomScreenEvent.RefreshData) },
                onSelectSection = { onClassroomEvent(ClassroomScreenEvent.SelectSection(it)) }
            )
            else -> TimetableDetailStep(
                semester = selectedSemester,
                section = selectedSection,
                selectedDay = selectedDay,
                selectedDayIndex = selectedDayIndex,
                onSelectDay = { selectedDayIndex = it },
                onBack = { onClassroomEvent(ClassroomScreenEvent.BackToSections) },
                onChangeSemester = { onClassroomEvent(ClassroomScreenEvent.BackToSemesters) }
            )
        }
    }
}

@Composable
private fun SemesterSelectionStep(
    semesterOptions: List<Int>,
    catalogState: ClassroomScreenState,
    onSelectSemester: (Int) -> Unit
) {
    TintedPanel {
        Text(
            text = "Choose your semester",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "The current XLS provides 4th semester data. Other semesters are already wired for future data drops.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            semesterOptions.chunked(2).forEach { rowSemesters ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    rowSemesters.forEach { semester ->
                        val sectionCount = catalogState.catalog.semesters
                            .firstOrNull { it.semesterNumber == semester }
                            ?.sections
                            ?.size
                            ?: 0
                        SelectionCard(
                            title = "Semester $semester",
                            subtitle = if (sectionCount > 0) "$sectionCount sections ready" else "No section data yet",
                            icon = Icons.Rounded.School,
                            modifier = Modifier.weight(1f),
                            onClick = { onSelectSemester(semester) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionSelectionStep(
    semester: Int,
    state: ClassroomScreenState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSelectSection: (String) -> Unit
) {
    val sections = state.catalog.semesters
        .firstOrNull { it.semesterNumber == semester }
        ?.sections
        .orEmpty()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepActionRow(
            primaryText = "Change semester",
            secondaryText = if (state.isRefreshing) "Refreshing..." else "Reload data",
            onPrimary = onBack,
            onSecondary = onRefresh,
            secondaryEnabled = !state.isRefreshing
        )

        if (sections.isEmpty()) {
            EmptyState(
                title = "No section data available for this semester yet.",
                description = "The flow is ready for Semester $semester, but the current XLS only contains populated timetable data for supported semesters."
            )
        } else {
            TintedPanel {
                Text(
                    text = "Select your section",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Semester $semester has ${sections.size} sections available from the imported XLS.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    sections.chunked(2).forEach { rowSections ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            rowSections.forEach { section ->
                                SelectionCard(
                                    title = section.sectionId,
                                    subtitle = "${section.days.count { it.entries.isNotEmpty() }} weekdays ready",
                                    icon = Icons.Rounded.Groups,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onSelectSection(section.sectionId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableDetailStep(
    semester: Int,
    section: ClassroomSection,
    selectedDay: ClassroomDaySchedule?,
    selectedDayIndex: Int,
    onSelectDay: (Int) -> Unit,
    onBack: () -> Unit,
    onChangeSemester: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StepActionRow(
            primaryText = "Change section",
            secondaryText = "Change semester",
            onPrimary = onBack,
            onSecondary = onChangeSemester
        )

        TintedPanel {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = section.sectionId,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Semester $semester • ${section.days.count { it.entries.isNotEmpty() }} imported weekdays",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(timetableShell, RoundedCornerShape(32.dp))
                .border(1.dp, timetableShellBorder, RoundedCornerShape(32.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                section.days.forEachIndexed { index, day ->
                    TimetableDayChip(
                        label = day.label,
                        selected = index == selectedDayIndex,
                        onClick = { onSelectDay(index) }
                    )
                }
            }

            if (selectedDay == null || selectedDay.entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(timetableDayIdle, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = timetableTextMuted
                            )
                        }
                        Text(
                            text = "No classes scheduled",
                            style = MaterialTheme.typography.titleMedium,
                            color = timetableTextPrimary
                        )
                        Text(
                            text = "Enjoy your free day!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = timetableTextMuted
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    selectedDay.entries.forEachIndexed { index, entry ->
                        TimetableEntryCard(
                            entry = entry,
                            highlighted = index == 0
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepActionRow(
    primaryText: String,
    secondaryText: String,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    secondaryEnabled: Boolean = true
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        LiquidGlassTextButton(
            text = primaryText,
            onClick = onPrimary,
            modifier = Modifier.weight(1f)
        )
        LiquidGlassTextButton(
            text = secondaryText,
            onClick = onSecondary,
            modifier = Modifier.weight(1f),
            enabled = secondaryEnabled
        )
    }
}

@Composable
private fun SelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(24.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LiquidGlassTextButton(
                text = "Open",
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TimetableDayChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(74.dp)
            .background(
                if (selected) timetableDaySelected else timetableDayIdle,
                RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Ink else timetableTextPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 6.dp)
        )
    }
}

@Composable
private fun TimetableEntryCard(
    entry: ClassroomScheduleEntry,
    highlighted: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (highlighted) timetableCardHighlight else timetableCard,
                RoundedCornerShape(26.dp)
            )
            .border(
                width = if (highlighted) 1.dp else 0.dp,
                color = if (highlighted) timetableAccent else Color.Transparent,
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 14.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(timetableAccent, RoundedCornerShape(99.dp))
                        .padding(horizontal = 2.dp, vertical = 24.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = entry.subject,
                        style = MaterialTheme.typography.titleLarge,
                        color = timetableTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.time,
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                        color = timetableTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = entry.room,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                color = timetableTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
