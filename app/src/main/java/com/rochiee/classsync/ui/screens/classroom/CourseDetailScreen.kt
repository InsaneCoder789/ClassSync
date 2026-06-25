package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rochiee.classsync.dashboard.CourseDashboardSummary
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.DeadlineText
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.StatRow
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDate
import com.rochiee.classsync.ui.theme.LocalSpacing

enum class CourseTab(val label: String) {
    Overview("Overview"),
    Tasks("Tasks"),
    Announcements("Announcements"),
    Materials("Materials"),
    Quizzes("Quizzes/Exams"),
    Activity("Activity"),
    Completed("Completed")
}

@Composable
fun CourseDetailScreen(
    summary: CourseDashboardSummary,
    selectedTab: CourseTab,
    onSelectTab: (CourseTab) -> Unit,
    selectedTasks: List<AcademicTask>,
    selectedEvents: List<ClassroomEvent>
) {
    val spacing = LocalSpacing.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = androidx.compose.ui.Modifier.horizontalScroll(rememberScrollState())
    ) {
        CourseTab.entries.forEach { tab ->
            LiquidGlassTextButton(
                text = tab.label,
                onClick = { onSelectTab(tab) },
                selected = selectedTab == tab,
                showArrow = false
            )
        }
    }

    when (selectedTab) {
        CourseTab.Overview -> TintedPanel {
            Text(text = summary.courseName, style = MaterialTheme.typography.titleLarge)
            summary.teacherName?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
            StatRow("Pending tasks", summary.pendingTaskCount.toString())
            StatRow("Overdue", summary.overdueCount.toString())
            StatRow("Quizzes / exams", summary.quizExamCount.toString())
            StatRow("Announcements", summary.announcementCount.toString())
            StatRow("Materials", summary.materialCount.toString())
            StatRow("Recent activity", summary.recentActivityCount.toString())
        }
        CourseTab.Tasks -> CourseTaskList(
            tasks = selectedTasks.filter { !it.isCompleted },
            emptyMessage = "No pending tasks for this course."
        )
        CourseTab.Announcements -> CourseSimpleList(
            items = selectedEvents.filter { it.eventType == ClassroomEventType.ANNOUNCEMENT }.map { it.title },
            emptyMessage = "No announcements stored for this course."
        )
        CourseTab.Materials -> CourseSimpleList(
            items = selectedEvents.filter { it.eventType == ClassroomEventType.MATERIAL }.map { it.title },
            emptyMessage = "No materials stored for this course."
        )
        CourseTab.Quizzes -> CourseSimpleList(
            items = selectedEvents.filter {
                it.eventType == ClassroomEventType.QUIZ || it.eventType == ClassroomEventType.EXAM
            }.map { "${it.title} • ${it.dueDateMillis.formatDate()}" },
            emptyMessage = "No quizzes or exams stored for this course."
        )
        CourseTab.Activity -> CourseSimpleList(
            items = selectedEvents.map { "${it.title} • ${it.eventType.name}" },
            emptyMessage = "No recent activity stored for this course."
        )
        CourseTab.Completed -> CourseSimpleList(
            items = selectedTasks.filter { it.isCompleted }.map { "${it.title} • completed" },
            emptyMessage = "No completed tasks yet for this course."
        )
    }
}

@Composable
private fun CourseSimpleList(
    items: List<String>,
    emptyMessage: String
) {
    val spacing = LocalSpacing.current
    if (items.isEmpty()) {
        EmptyState("Nothing here yet", emptyMessage)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            items.forEach { item ->
                TintedPanel(modifier = Modifier.fillMaxWidth()) {
                    Text(text = item, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun CourseTaskList(
    tasks: List<AcademicTask>,
    emptyMessage: String
) {
    val spacing = LocalSpacing.current
    if (tasks.isEmpty()) {
        EmptyState("Nothing here yet", emptyMessage)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            tasks.forEach { task ->
                TintedPanel(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = task.courseName, style = MaterialTheme.typography.bodyMedium)
                        ResponsiveFlowRow(maxItemsInEachRow = 2) {
                            DeadlineChip(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                            if (task.description.isNotBlank()) {
                                Text(text = task.description, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        DeadlineText(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                    }
                }
            }
        }
    }
}
