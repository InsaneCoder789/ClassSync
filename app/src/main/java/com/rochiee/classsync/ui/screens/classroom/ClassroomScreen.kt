package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.classroom.ClassroomScreenEvent
import com.rochiee.classsync.bloc.classroom.ClassroomScreenState
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.dashboard.CourseDashboardSummary
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineTone
import com.rochiee.classsync.ui.components.deadlineToneFor
import com.rochiee.classsync.ui.theme.LocalSpacing
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val scheduleDayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
private val scheduleDateFormatter = SimpleDateFormat("d", Locale.getDefault())
private val scheduleTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

@Composable
fun ClassroomScreen(
    classroomState: ClassroomScreenState,
    onClassroomEvent: (ClassroomScreenEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var selectedTab by remember { mutableStateOf(CourseTab.Overview) }
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
    val filteredCourseIds = filteredCourses.map { it.courseId }.toSet()
    val filteredCourseNames = filteredCourses.map { it.name }.toSet()
    val filteredSummaries = classroomState.courseSummaries.filter { it.courseId in filteredCourseIds }
    val filteredTasks = classroomState.allTasks.filter { it.courseName in filteredCourseNames }
    val filteredEvents = classroomState.allEvents.filter {
        it.courseId in filteredCourseIds || it.courseName in filteredCourseNames
    }
    val sectionOptions = remember(filteredCourses) {
        filteredCourses.mapNotNull { it.section }.distinct().sorted()
    }
    val courseLookup = remember(filteredCourses) { filteredCourses.associateBy { it.name } }
    val weeklyTimeline = remember(filteredTasks, filteredEvents, courseLookup) {
        buildWeeklyTimeline(filteredTasks, filteredEvents, courseLookup)
    }
    val selectedDay = weeklyTimeline.getOrNull(selectedDayIndex) ?: weeklyTimeline.firstOrNull()

    val selectedCourseId = classroomState.selectedCourseId.takeIf { id -> id in filteredCourseIds }
        ?: filteredSummaries.firstOrNull()?.courseId
    val selectedSummary = filteredSummaries.firstOrNull { it.courseId == selectedCourseId }

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
                text = "Semester $selectedSemester${selectedSection?.let { " • section $it" } ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TintedPanel {
            Text(
                text = "Semester",
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
            Text(
                text = "Section",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
            weeklyTimeline.forEachIndexed { index, day ->
                DayTab(
                    label = day.label,
                    selected = index == selectedDayIndex,
                    onClick = { selectedDayIndex = index },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            selectedDay?.items?.ifEmpty {
                listOf(
                    ScheduleItem(
                        title = "No dated classroom items",
                        timeLabel = "No timed work found",
                        roomLabel = "Refresh or change section",
                        accentColor = MaterialTheme.colorScheme.primary,
                        courseId = null
                    )
                )
            }?.forEach { item ->
                ScheduleBlockCard(item = item)
            }
        }

        CourseListScreen(
            summaries = filteredSummaries,
            isRefreshing = classroomState.isRefreshing,
            onRefresh = { onClassroomEvent(ClassroomScreenEvent.RefreshCourses) },
            onSelectCourse = { courseId -> onClassroomEvent(ClassroomScreenEvent.SelectCourse(courseId)) }
        )

        selectedSummary?.let { summary ->
            CourseDetailScreen(
                summary = summary,
                selectedTab = selectedTab,
                onSelectTab = { selectedTab = it },
                selectedTasks = classroomState.selectedCourseTasks,
                selectedEvents = classroomState.selectedCourseEvents
            )
        }
    }
}

@Composable
private fun DayTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glow = if (selected) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = if (selected) {
                        listOf(
                            glow,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                        )
                    } else {
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                        )
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(2.dp)
    ) {
        LiquidGlassTextButton(
            text = label.uppercase(),
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            selected = selected,
            showArrow = false
        )
    }
}

@Composable
private fun ScheduleBlockCard(item: ScheduleItem) {
    TintedPanel(accentColor = item.accentColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(item.accentColor, RoundedCornerShape(99.dp))
                        .padding(horizontal = 2.dp, vertical = 26.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.timeLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = item.roomLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class WeeklyTimelineDay(
    val label: String,
    val items: List<ScheduleItem>
)

private data class ScheduleItem(
    val title: String,
    val timeLabel: String,
    val roomLabel: String,
    val accentColor: androidx.compose.ui.graphics.Color,
    val courseId: String?
)

private fun buildWeeklyTimeline(
    tasks: List<AcademicTask>,
    events: List<ClassroomEvent>,
    coursesByName: Map<String, CourseEntity>
): List<WeeklyTimelineDay> {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        val currentDay = get(Calendar.DAY_OF_WEEK)
        val diff = if (currentDay == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDay
        add(Calendar.DAY_OF_MONTH, diff)
    }

    return (0 until 7).map { offset ->
        val start = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }
        val end = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1); add(Calendar.MILLISECOND, -1) }
        val startMillis = start.timeInMillis
        val endMillis = end.timeInMillis

        val taskItems = tasks.filter { task ->
            task.dueDate?.let { it in startMillis..endMillis } == true
        }.map { task ->
            val course = coursesByName[task.courseName]
            val tone = task.deadlineTone()
            ScheduleItem(
                title = task.courseName,
                timeLabel = formatScheduleTime(task.dueDate),
                roomLabel = course?.room ?: course?.section ?: "Classroom",
                accentColor = tone.color,
                courseId = course?.courseId
            )
        }

        val eventItems = events.filter { event ->
            val anchor = event.dueDateMillis ?: event.eventTimeMillis
            anchor in startMillis..endMillis
        }.map { event ->
            val course = coursesByName[event.courseName]
            val tone = deadlineToneFor(event.dueDateMillis, false)
            ScheduleItem(
                title = event.courseName ?: "Classroom",
                timeLabel = formatScheduleTime(event.dueDateMillis ?: event.eventTimeMillis),
                roomLabel = course?.room ?: course?.section ?: "Live event",
                accentColor = tone.color,
                courseId = event.courseId
            )
        }

        WeeklyTimelineDay(
            label = scheduleDayFormatter.format(Date(startMillis)).uppercase(),
            items = (taskItems + eventItems)
                .sortedBy { parseScheduleSortTime(it.timeLabel) }
                .distinctBy { "${it.title}:${it.timeLabel}:${it.roomLabel}" }
        )
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

private fun formatScheduleTime(timeMillis: Long?): String {
    if (timeMillis == null) return "No time"
    val start = scheduleTimeFormatter.format(Date(timeMillis))
    val end = scheduleTimeFormatter.format(Date(timeMillis + 60L * 60L * 1000L))
    return "$start - $end"
}

private fun parseScheduleSortTime(label: String): Long {
    return runCatching {
        val start = label.substringBefore(" - ")
        SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(start)?.time ?: Long.MAX_VALUE
    }.getOrDefault(Long.MAX_VALUE)
}
