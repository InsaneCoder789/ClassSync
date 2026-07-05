package com.rochiee.classsync.ui.screens.study

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.study.StudyPlanEvent
import com.rochiee.classsync.bloc.study.StudyPlanState
import com.rochiee.classsync.study.StudyPlanItem
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.StatusChip
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDate
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.SafeGreen
import com.rochiee.classsync.ui.theme.SilverBorder
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StudyPlannerScreen(
    state: StudyPlanState,
    onEvent: (StudyPlanEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val plan = state.plan
    val items = plan?.items.orEmpty()
    val availableCourses = state.availableCourses
    val selectedCourseIds = state.selectedCourseIds
    val selectedCourseCount = selectedCourseIds.size
    val manualItems = remember(items) { items.filter { it.isManual } }
    val suggestedItems = remember(items) { items.filterNot { it.isManual } }
    val completedCount = remember(items) { items.count { it.isDone } }
    val activeCount = (items.size - completedCount).coerceAtLeast(0)

    var showComposer by remember { mutableStateOf(false) }
    var manualTitle by remember { mutableStateOf("") }
    var manualCourse by remember { mutableStateOf("") }
    var manualNotes by remember { mutableStateOf("") }
    var scheduledMillis by remember { mutableStateOf<Long?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = scheduledMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    calendar.set(Calendar.MINUTE, minute)
                                    calendar.set(Calendar.SECOND, 0)
                                    calendar.set(Calendar.MILLISECOND, 0)
                                    scheduledMillis = calendar.timeInMillis
                                },
                                18,
                                0,
                                false
                            ).show()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pick time")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        modifier = Modifier.padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        item {
            ScreenSection(
                title = "Study planner",
                subtitle = "Choose the courses you actually want to focus on, then let ClassSync build a study queue around them."
            ) {
                TintedPanel {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        SilverBorder.copy(alpha = 0.18f)
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "STUDY PLAN",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Start with your real course list, pick the subjects you want to work on, then generate a plan that stays aligned with your own intent.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ResponsiveFlowRow(maxItemsInEachRow = 2) {
                        LiquidGlassTextButton(
                            text = if (state.isLoading) "Generating..." else "Generate study plan",
                            onClick = { onEvent(StudyPlanEvent.GeneratePlan) },
                            modifier = Modifier.fillMaxWidth(),
                            selected = false,
                            enabled = !state.isLoading
                        )
                        LiquidGlassTextButton(
                            text = if (showComposer) "Hide manual block" else "Add manual block",
                            onClick = {
                                showComposer = !showComposer
                                if (!showComposer) localError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            selected = showComposer
                        )
                    }
                }
            }
        }

        item {
            ScreenSection(
                title = "Course selection",
                subtitle = "Every synced class appears here first. Pick only the courses you want this plan to cover."
            ) {
                TintedPanel {
                    Text(
                        text = if (availableCourses.isEmpty()) {
                            "No synced Classroom courses are available yet."
                        } else {
                            "$selectedCourseCount of ${availableCourses.size} course${if (availableCourses.size == 1) "" else "s"} selected."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (availableCourses.isEmpty()) {
                            "Run a Classroom sync first, then come back here to choose which classes should feed your study plan."
                        } else {
                            "Your plan will only use tasks and academic events from the selected courses."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (availableCourses.isNotEmpty()) {
                        ResponsiveFlowRow(maxItemsInEachRow = 2) {
                            LiquidGlassTextButton(
                                text = "Select all courses",
                                onClick = { onEvent(StudyPlanEvent.SelectAllCourses) },
                                modifier = Modifier.fillMaxWidth(),
                                selected = selectedCourseCount == availableCourses.size
                            )
                            LiquidGlassTextButton(
                                text = "Clear selection",
                                onClick = { onEvent(StudyPlanEvent.ClearCourseSelection) },
                                modifier = Modifier.fillMaxWidth(),
                                selected = selectedCourseCount == 0
                            )
                        }
                        ResponsiveFlowRow(maxItemsInEachRow = 2) {
                            availableCourses.forEach { course ->
                                LiquidGlassTextButton(
                                    text = course.name,
                                    onClick = { onEvent(StudyPlanEvent.ToggleCourseSelection(course.courseId)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    selected = selectedCourseIds.contains(course.courseId)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            TintedPanel {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "Plan state",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (plan == null) "No plan generated yet" else "Last refreshed ${plan.generatedAtMillis.formatDateTime()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Suggested blocks regenerate only from the courses you selected. Manual blocks stay pinned so your own study routine does not disappear on refresh.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    if (maxWidth < 420.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                ElevatedInfoCard(
                                    title = "Total blocks",
                                    value = items.size.toString(),
                                    supportingText = "Everything currently scheduled across suggested and manual sessions",
                                    modifier = Modifier.weight(1f),
                                    accent = SkyBlue
                                )
                                ElevatedInfoCard(
                                    title = "Active focus",
                                    value = activeCount.toString(),
                                    supportingText = "Blocks still waiting for your attention",
                                    modifier = Modifier.weight(1f),
                                    accent = Sun
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                ElevatedInfoCard(
                                    title = "Completed",
                                    value = completedCount.toString(),
                                    supportingText = "Sessions you have already checked off",
                                    modifier = Modifier.weight(1f),
                                    accent = SafeGreen
                                )
                                ElevatedInfoCard(
                                    title = "Manual",
                                    value = manualItems.size.toString(),
                                    supportingText = "Blocks you added yourself for revision and catch-up",
                                    modifier = Modifier.weight(1f),
                                    accent = MintGreen
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                ElevatedInfoCard(
                                    title = "Total blocks",
                                    value = items.size.toString(),
                                    supportingText = "Everything currently scheduled across suggested and manual sessions",
                                    modifier = Modifier.weight(1f),
                                    accent = SkyBlue
                                )
                                ElevatedInfoCard(
                                    title = "Active focus",
                                    value = activeCount.toString(),
                                    supportingText = "Blocks still waiting for your attention",
                                    modifier = Modifier.weight(1f),
                                    accent = Sun
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                ElevatedInfoCard(
                                    title = "Completed",
                                    value = completedCount.toString(),
                                    supportingText = "Sessions you have already checked off",
                                    modifier = Modifier.weight(1f),
                                    accent = SafeGreen
                                )
                                ElevatedInfoCard(
                                    title = "Manual",
                                    value = manualItems.size.toString(),
                                    supportingText = "Blocks you added yourself for revision and catch-up",
                                    modifier = Modifier.weight(1f),
                                    accent = MintGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showComposer) {
            item {
                TintedPanel {
                    Text(
                        text = "Manual study block",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = manualTitle,
                        onValueChange = { manualTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Block title") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = manualCourse,
                        onValueChange = { manualCourse = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Course or subject") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = scheduledMillis.formatManualDate(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        label = { Text("Planned time") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.DateRange,
                                    contentDescription = "Pick date and time"
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        value = manualNotes,
                        onValueChange = { manualNotes = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Focus note") },
                        minLines = 3
                    )
                    localError?.let { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    ResponsiveFlowRow(maxItemsInEachRow = 2) {
                        LiquidGlassTextButton(
                            text = "Save block",
                            onClick = {
                                when {
                                    manualTitle.isBlank() -> localError = "Give the study block a short title."
                                    scheduledMillis == null -> localError = "Pick when you want to study."
                                    else -> {
                                        onEvent(
                                            StudyPlanEvent.AddManualBlock(
                                                title = manualTitle.trim(),
                                                courseName = manualCourse.trim(),
                                                scheduledDateMillis = scheduledMillis!!,
                                                notes = manualNotes.trim()
                                            )
                                        )
                                        manualTitle = ""
                                        manualCourse = ""
                                        manualNotes = ""
                                        scheduledMillis = null
                                        localError = null
                                        showComposer = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            selected = true
                        )
                        LiquidGlassTextButton(
                            text = "Clear fields",
                            onClick = {
                                manualTitle = ""
                                manualCourse = ""
                                manualNotes = ""
                                scheduledMillis = null
                                localError = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            ScreenSection(
                title = "Focus queue",
                subtitle = "Suggested sessions stay alongside the manual ones you care about most."
            ) {
                if (items.isEmpty()) {
                    EmptyState(
                        "No study plan yet",
                        "Generate a suggested plan or add a manual block to start shaping your study week."
                    )
                }
            }
        }

        if (suggestedItems.isNotEmpty()) {
            item {
                Text(
                    text = "Suggested sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(suggestedItems, key = { it.id }) { item ->
                StudyPlanItemCard(
                    item = item,
                    onToggleDone = { onEvent(StudyPlanEvent.ToggleBlockDone(item.id)) },
                    onDelete = null
                )
            }
        }

        if (manualItems.isNotEmpty()) {
            item {
                Text(
                    text = "Manual sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(manualItems, key = { it.id }) { item ->
                StudyPlanItemCard(
                    item = item,
                    onToggleDone = { onEvent(StudyPlanEvent.ToggleBlockDone(item.id)) },
                    onDelete = { onEvent(StudyPlanEvent.DeleteBlock(item.id)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudyPlanItemCard(
    item: StudyPlanItem,
    onToggleDone: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val spacing = LocalSpacing.current
    val accent = when {
        item.isDone -> SafeGreen
        item.isManual -> MintGreen
        else -> SkyBlue
    }

    TintedPanel(accentColor = accent) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
                ResponsiveFlowRow(maxItemsInEachRow = 3) {
                    StatusChip(label = item.courseName, color = SkyBlue)
                    StatusChip(label = item.sourceType, color = if (item.isManual) MintGreen else SafeGreen)
                    DeadlineChip(dueMillis = item.scheduledDateMillis, isCompleted = item.isDone)
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete study block"
                    )
                }
            }
        }
        Text(
            text = item.scheduledDateMillis.formatDate(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = item.priorityExplanation,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.estimatedEffortLabel,
            style = MaterialTheme.typography.bodySmall,
            color = accent
        )
        if (item.notes.isNotBlank()) {
            Text(
                text = item.notes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LiquidGlassTextButton(
            text = if (item.isDone) "Mark active again" else "Complete session",
            onClick = onToggleDone,
            modifier = Modifier.fillMaxWidth(),
            selected = item.isDone
        )
    }
}

private fun Long?.formatManualDate(): String {
    if (this == null) return "Pick date and time"
    return SimpleDateFormat("EEE, dd MMM yyyy • h:mm a", Locale.getDefault()).format(Date(this))
}
