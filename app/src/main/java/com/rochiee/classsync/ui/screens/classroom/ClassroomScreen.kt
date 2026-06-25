package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing
import java.util.Calendar

private val scheduleShell = Color(0xFF141018)
private val scheduleShellBorder = Color(0xFF2A202C)
private val scheduleCard = Color(0xFF3A2B39)
private val scheduleCardHighlight = Color(0xFF8A4B22)
private val scheduleTabIdle = Color(0xFF2B222E)
private val scheduleTabSelected = Color(0xFFC38528)
private val scheduleAccent = Color(0xFFFFA53C)
private val scheduleTextPrimary = Color(0xFFF8F2F6)
private val scheduleTextMuted = Color(0xFFD9C9D3)

@Composable
fun ClassroomScreen(
    classroomState: ClassroomScreenState,
    onClassroomEvent: (ClassroomScreenEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var selectedSemester by remember { mutableStateOf("4") }
    var selectedSection by remember { mutableStateOf<String?>(null) }
    var selectedDayIndex by remember { mutableIntStateOf(currentWeekIndex()) }

    val semesterOptions = remember(classroomState.courses) {
        buildList {
            add("4")
            classroomState.courses
                .mapNotNull { deriveSemester(it.name, it.section) }
                .distinct()
                .filterNot { it == "4" }
                .sorted()
                .forEach(::add)
        }
    }
    val filteredCourses = remember(classroomState.courses, selectedSemester, selectedSection) {
        classroomState.courses.filter { course ->
            val semesterMatches = deriveSemester(course.name, course.section) == selectedSemester
            val sectionMatches = selectedSection == null || course.section == selectedSection
            semesterMatches && sectionMatches
        }
    }
    val sectionOptions = remember(filteredCourses) {
        filteredCourses.mapNotNull { it.section }.distinct().sorted()
    }
    val timetableWeek = remember(filteredCourses, classroomState.allTasks) {
        buildTimetableWeek(filteredCourses, classroomState.allTasks)
    }
    val selectedDay = timetableWeek.getOrNull(selectedDayIndex) ?: timetableWeek.first()

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
                text = "Schedule",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Semester $selectedSemester${selectedSection?.let { " • section $it" } ?: ""} timetable",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TintedPanel {
            Text(
                text = "Timetable filters",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                semesterOptions.take(3).forEach { semester ->
                    LiquidGlassTextButton(
                        text = "Sem $semester",
                        onClick = {
                            selectedSemester = semester
                            selectedSection = null
                        },
                        modifier = Modifier.weight(1f),
                        selected = selectedSemester == semester,
                        showArrow = false
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(
                    text = "All",
                    onClick = { selectedSection = null },
                    modifier = Modifier.weight(1f),
                    selected = selectedSection == null,
                    showArrow = false
                )
                sectionOptions.take(2).forEach { section ->
                    LiquidGlassTextButton(
                        text = section,
                        onClick = { selectedSection = section },
                        modifier = Modifier.weight(1f),
                        selected = selectedSection == section,
                        showArrow = false
                    )
                }
            }
            LiquidGlassTextButton(
                text = if (classroomState.isRefreshing) "Refreshing..." else "Refresh schedule",
                onClick = { onClassroomEvent(ClassroomScreenEvent.RefreshCourses) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !classroomState.isRefreshing
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(scheduleShell, RoundedCornerShape(32.dp))
                .border(1.dp, scheduleShellBorder, RoundedCornerShape(32.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Weekly board",
                        style = MaterialTheme.typography.titleLarge,
                        color = scheduleTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${filteredCourses.size} subjects mapped into a class-first schedule",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheduleTextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .background(scheduleTabIdle, RoundedCornerShape(18.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = selectedSection ?: "All sections",
                        style = MaterialTheme.typography.labelLarge,
                        color = scheduleTextPrimary
                    )
                }
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                timetableWeek.forEachIndexed { index, day ->
                    ScheduleDayTab(
                        label = day.label,
                        selected = index == selectedDayIndex,
                        onClick = { selectedDayIndex = index }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (selectedDay.entries.isEmpty()) {
                    ScheduleEmptyCard()
                } else {
                    selectedDay.entries.forEachIndexed { index, entry ->
                        ScheduleLessonCard(
                            entry = entry,
                            highlighted = index == 0 && selectedDayIndex == currentWeekIndex()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleDayTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(68.dp)
            .background(
                color = if (selected) scheduleTabSelected else scheduleTabIdle,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(2.dp)
    ) {
        LiquidGlassTextButton(
            text = label,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            selected = selected,
            showArrow = false
        )
    }
}

@Composable
private fun ScheduleLessonCard(
    entry: TimetableEntry,
    highlighted: Boolean
) {
    val cardColor = if (highlighted || entry.emphasis) scheduleCardHighlight else scheduleCard
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor, RoundedCornerShape(24.dp))
            .border(
                width = if (highlighted) 1.dp else 0.dp,
                color = if (highlighted) scheduleAccent.copy(alpha = 0.9f) else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 14.dp, vertical = 16.dp)
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
                        .background(scheduleAccent, RoundedCornerShape(99.dp))
                        .padding(horizontal = 2.dp, vertical = 22.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = scheduleTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.timeLabel,
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                        color = scheduleTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = entry.roomLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                color = scheduleTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScheduleEmptyCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(scheduleCard, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "No classes mapped for this day",
                style = MaterialTheme.typography.titleMedium,
                color = scheduleTextPrimary
            )
            Text(
                text = "Try another section or refresh after your Classroom sync completes.",
                style = MaterialTheme.typography.bodyMedium,
                color = scheduleTextMuted
            )
        }
    }
}

private data class TimetableDay(
    val label: String,
    val entries: List<TimetableEntry>
)

private data class TimetableEntry(
    val dayIndex: Int,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val title: String,
    val roomLabel: String,
    val emphasis: Boolean
) {
    val timeLabel: String
        get() = "${formatHour(startHour, startMinute)} - ${formatHour(endHour, endMinute)}"
}

private data class TimetableSlot(
    val dayIndex: Int,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
)

private fun buildTimetableWeek(
    courses: List<CourseEntity>,
    tasks: List<com.rochiee.classsync.domain.model.AcademicTask>
): List<TimetableDay> {
    val sortedCourses = courses
        .sortedBy { courseShortTitle(it.name) }

    val urgentCourses = tasks
        .filter { !it.isCompleted && it.dueDate != null }
        .sortedBy { it.dueDate }
        .take(4)
        .map { it.courseName }
        .toSet()

    val slotTemplates = listOf(
        TimetableSlot(0, 8, 0, 9, 0),
        TimetableSlot(0, 9, 0, 10, 0),
        TimetableSlot(0, 10, 0, 11, 0),
        TimetableSlot(1, 11, 0, 12, 0),
        TimetableSlot(1, 12, 0, 13, 0),
        TimetableSlot(2, 8, 0, 9, 0),
        TimetableSlot(2, 9, 0, 10, 0),
        TimetableSlot(2, 10, 0, 11, 0),
        TimetableSlot(3, 8, 0, 9, 0),
        TimetableSlot(3, 9, 0, 10, 0),
        TimetableSlot(3, 10, 0, 11, 0),
        TimetableSlot(3, 11, 0, 12, 0),
        TimetableSlot(3, 12, 0, 13, 0),
        TimetableSlot(3, 13, 0, 14, 0),
        TimetableSlot(4, 8, 0, 9, 0),
        TimetableSlot(4, 9, 0, 10, 0),
        TimetableSlot(4, 10, 0, 11, 0),
        TimetableSlot(5, 9, 0, 10, 0),
        TimetableSlot(5, 10, 0, 11, 0)
    )

    val flattenedEntries = buildList {
        var slotIndex = 0
        sortedCourses.forEach { course ->
            val occurrenceCount = if (course.name.contains("(L)", ignoreCase = true)) 2 else 1
            repeat(occurrenceCount) {
                val slot = slotTemplates[slotIndex % slotTemplates.size]
                add(
                    TimetableEntry(
                        dayIndex = slot.dayIndex,
                        startHour = slot.startHour,
                        startMinute = slot.startMinute,
                        endHour = slot.endHour,
                        endMinute = slot.endMinute,
                        title = courseShortTitle(course.name),
                        roomLabel = course.room ?: course.section ?: "Classroom",
                        emphasis = course.name in urgentCourses
                    )
                )
                slotIndex++
            }
        }
    }

    val dayLabels = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    return dayLabels.mapIndexed { index, label ->
        TimetableDay(
            label = label,
            entries = flattenedEntries
                .filter { it.dayIndex == index }
                .sortedWith(compareBy<TimetableEntry> { it.startHour }.thenBy { it.startMinute })
        )
    }
}

private fun courseShortTitle(name: String): String {
    val cleaned = name
        .replace(Regex("""semester\s*\d+""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""section\s*[a-z0-9-]+""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\s+"""), " ")
        .trim()
    return when {
        cleaned.length <= 14 -> cleaned
        else -> cleaned
            .split(" ")
            .filter { it.isNotBlank() }
            .map { token -> token.first().uppercaseChar() }
            .joinToString("")
            .ifBlank { cleaned.take(14) }
    }
}

private fun deriveSemester(courseName: String, section: String?): String? {
    val fullText = listOf(courseName, section.orEmpty()).joinToString(" ").lowercase()
    return Regex("""(?:sem(?:ester)?)\s*([1-8])|([1-8])(?:st|nd|rd|th)\s*sem""")
        .find(fullText)
        ?.groupValues
        ?.drop(1)
        ?.firstOrNull { it.isNotBlank() }
        ?: if (fullText.contains("4th") || fullText.contains("sem 4") || fullText.contains("semester 4")) "4" else null
}

private fun currentWeekIndex(): Int {
    return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        else -> 6
    }
}

private fun formatHour(hour24: Int, minute: Int): String {
    val suffix = if (hour24 >= 12) "PM" else "AM"
    val displayHour = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    return "%02d:%02d %s".format(displayHour, minute, suffix)
}
