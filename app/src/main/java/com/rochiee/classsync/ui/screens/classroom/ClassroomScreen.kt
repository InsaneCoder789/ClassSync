package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.rochiee.classsync.bloc.classroom.ClassroomScreenEvent
import com.rochiee.classsync.bloc.classroom.ClassroomScreenState
import com.rochiee.classsync.dashboard.CourseDashboardSummary
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineToneFor
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val weekDayLabelFormatter = SimpleDateFormat("EEE", Locale.getDefault())
private val dayNumberFormatter = SimpleDateFormat("d", Locale.getDefault())

@Composable
fun ClassroomScreen(
    classroomState: ClassroomScreenState,
    onClassroomEvent: (ClassroomScreenEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var selectedTab by remember { mutableStateOf(CourseTab.Overview) }
    var selectedSemester by remember { mutableStateOf("4") }
    var selectedSection by remember { mutableStateOf<String?>(null) }

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

    val selectedCourseId = classroomState.selectedCourseId.takeIf { id -> id in filteredCourseIds }
        ?: filteredSummaries.firstOrNull()?.courseId
    val selectedSummary = filteredSummaries.firstOrNull { it.courseId == selectedCourseId }
    val weeklyTimeline = remember(filteredTasks, filteredEvents) {
        buildWeeklyTimeline(filteredTasks, filteredEvents)
    }

    Column(
        modifier = Modifier
            .padding(spacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        ScreenSection(
            title = "Semester classroom timetable",
            subtitle = "A 7-day live board for semester-based Classroom work, filtered by the section you choose."
        ) {
            ResponsiveFlowRow(maxItemsInEachRow = 2) {
                LiquidGlassTextButton(
                    text = "Semester $selectedSemester",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    showArrow = false,
                    selected = true
                )
                LiquidGlassTextButton(
                    text = selectedSection ?: "All sections",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    showArrow = false,
                    selected = true
                )
            }
            TintedPanel {
                Text(
                    text = "Semester",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ResponsiveFlowRow(maxItemsInEachRow = 3) {
                    semesterOptions.forEach { semester ->
                        LiquidGlassTextButton(
                            text = "Sem $semester",
                            onClick = {
                                selectedSemester = semester
                                selectedSection = null
                            },
                            modifier = Modifier.fillMaxWidth(),
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
                ResponsiveFlowRow(maxItemsInEachRow = 3) {
                    LiquidGlassTextButton(
                        text = "All",
                        onClick = { selectedSection = null },
                        modifier = Modifier.fillMaxWidth(),
                        selected = selectedSection == null,
                        showArrow = false
                    )
                    sectionOptions.forEach { section ->
                        LiquidGlassTextButton(
                            text = section,
                            onClick = { selectedSection = section },
                            modifier = Modifier.fillMaxWidth(),
                            selected = selectedSection == section,
                            showArrow = false
                        )
                    }
                }
                LiquidGlassTextButton(
                    text = if (classroomState.isRefreshing) "Refreshing..." else "Refresh Classroom Data",
                    onClick = { onClassroomEvent(ClassroomScreenEvent.RefreshCourses) },
                    modifier = Modifier.fillMaxWidth(),
                    showArrow = false
                )
            }
            ResponsiveFlowRow(maxItemsInEachRow = 1) {
                ElevatedInfoCard(
                    title = "Active courses",
                    value = filteredSummaries.size.toString(),
                    supportingText = "Semester $selectedSemester course spaces in the current filter",
                    modifier = Modifier.fillMaxWidth(),
                    accent = SkyBlue
                )
                ElevatedInfoCard(
                    title = "This week",
                    value = weeklyTimeline.sumOf { it.items.size }.toString(),
                    supportingText = "Live task and event items visible across the next seven days",
                    modifier = Modifier.fillMaxWidth(),
                    accent = Sun
                )
                ElevatedInfoCard(
                    title = "Overdue",
                    value = filteredTasks.count { !it.isCompleted && (it.dueDate ?: Long.MAX_VALUE) < System.currentTimeMillis() }.toString(),
                    supportingText = "Semester items already behind schedule",
                    modifier = Modifier.fillMaxWidth(),
                    accent = Negative
                )
            }
        }

        ScreenSection(title = "7-day board", subtitle = "Live Classroom data grouped through the week for the selected semester and section.") {
            if (weeklyTimeline.all { it.items.isEmpty() }) {
                TintedPanel {
                    Text(
                        text = "No dated Classroom work is available for this semester/section yet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    weeklyTimeline.forEach { day ->
                        WeeklyTimetableDayCard(day = day)
                    }
                }
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
private fun WeeklyTimetableDayCard(day: WeeklyTimetableDay) {
    val spacing = LocalSpacing.current
    val firstDue = day.items.minByOrNull { it.dueMillis ?: Long.MAX_VALUE }
    val tone = deadlineToneFor(firstDue?.dueMillis, false)

    TintedPanel(
        accentColor = if (day.items.isEmpty()) null else tone.color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = day.dateNumber,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (day.items.isEmpty()) MaterialTheme.colorScheme.onSurface else tone.color
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Text(
                    text = if (day.items.isEmpty()) "No classroom load" else "${day.items.size} live item${if (day.items.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.titleMedium
                )
                day.items.take(3).forEach { item ->
                    Text(
                        text = "${item.courseLabel}: ${item.title}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private data class WeeklyTimetableDay(
    val label: String,
    val dateNumber: String,
    val items: List<TimetableItem>
)

private data class TimetableItem(
    val courseLabel: String,
    val title: String,
    val dueMillis: Long?
)

private fun buildWeeklyTimeline(
    tasks: List<AcademicTask>,
    events: List<ClassroomEvent>
): List<WeeklyTimetableDay> {
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
        }.map {
            TimetableItem(
                courseLabel = it.courseName,
                title = it.title,
                dueMillis = it.dueDate
            )
        }
        val eventItems = events.filter { event ->
            val date = event.dueDateMillis ?: event.eventTimeMillis
            date in startMillis..endMillis
        }.map {
            TimetableItem(
                courseLabel = it.courseName ?: "Classroom",
                title = it.title,
                dueMillis = it.dueDateMillis
            )
        }

        WeeklyTimetableDay(
            label = weekDayLabelFormatter.format(Date(startMillis)),
            dateNumber = dayNumberFormatter.format(Date(startMillis)),
            items = (taskItems + eventItems).distinctBy { "${it.courseLabel}:${it.title}:${it.dueMillis}" }
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
