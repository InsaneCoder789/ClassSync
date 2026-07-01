package com.rochiee.classsync.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.event.EventState
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.ui.components.ClassSyncProgressWidget
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineTone
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SafeGreen
import com.rochiee.classsync.ui.theme.SilverBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val homeTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    authState: AuthUiState,
    taskState: TaskState,
    syncState: SyncState,
    eventState: EventState,
    onNavigateToActivity: () -> Unit,
    onNavigateToStudyPlanner: () -> Unit,
    onNavigateToExamMode: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val spacing = LocalSpacing.current
    val openTasks = taskState.tasks.filter { !it.isCompleted && it.dueDate != null }.sortedBy { it.dueDate }
    val now = System.currentTimeMillis()
    val urgentCount = openTasks.count { task ->
        val due = task.dueDate ?: return@count false
        due <= now + 24L * 60L * 60L * 1000L
    }
    val ongoingTask = openTasks.firstOrNull { task ->
        val due = task.dueDate ?: return@firstOrNull false
        due in now..(now + 2L * 60L * 60L * 1000L)
    } ?: openTasks.firstOrNull()
    val upcomingTasks = openTasks.filterNot { it.id == ongoingTask?.id }.take(3)
    val totalTasks = taskState.tasks.size
    val completedTasks = taskState.tasks.count { it.isCompleted }
    val tasksLeft = (totalTasks - completedTasks).coerceAtLeast(0)
    val progressPercent = if (totalTasks == 0) 0 else ((completedTasks * 100f) / totalTasks).roundToInt()
    val dueTodayCount = openTasks.count { task ->
        val due = task.dueDate ?: return@count false
        due <= now + 24L * 60L * 60L * 1000L
    }
    val overdueCount = openTasks.count { task ->
        val due = task.dueDate ?: return@count false
        due < now
    }
    val upcomingAssessments = eventState.allEvents.count {
        (it.eventType == ClassroomEventType.QUIZ || it.eventType == ClassroomEventType.EXAM) &&
            (it.dueDateMillis ?: Long.MAX_VALUE) >= now
    }
    val digestHeadline = eventState.announcements.firstOrNull()?.title
        ?: eventState.materials.firstOrNull()?.title
        ?: ongoingTask?.title

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.26f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        item {
            TintedPanel {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "LIVE OVERVIEW",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Your academic day, arranged by urgency.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (authState.isSignedIn) {
                            "Signed in as ${authState.userEmail ?: authState.displayName ?: "student"} and ready to capture changes across Classroom, tasks, and upcoming assessments."
                        } else {
                            "Connect Google from settings to unlock live Classroom timelines and richer course context."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ClassSyncProgressWidget(
                    progressPercent = progressPercent.coerceIn(0, 100),
                    completedTasks = completedTasks,
                    totalTasks = totalTasks,
                    tasksLeft = tasksLeft,
                    progressCaption = if (tasksLeft == 0) {
                        "All current work is under control"
                    } else {
                        "$tasksLeft live academic item${if (tasksLeft == 1) "" else "s"} are still active"
                    }
                )
                ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    ElevatedInfoCard(
                        title = "Open work",
                        value = openTasks.size.toString(),
                        supportingText = "Incomplete assignments currently in your queue",
                        modifier = Modifier.fillMaxWidth(),
                        accent = MaterialTheme.colorScheme.primary
                    )
                    ElevatedInfoCard(
                        title = "Urgent soon",
                        value = urgentCount.toString(),
                        supportingText = "Assignments due within the next 24 hours",
                        modifier = Modifier.fillMaxWidth(),
                        accent = if (urgentCount > 0) Negative else SafeGreen
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = "Now in motion".uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ScheduleFeatureCard(
                    task = ongoingTask,
                    fallbackTitle = "No live assignment right now",
                    fallbackSubtitle = "Your next timed work will appear here once Classroom dates are synced."
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = "Coming up next".uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (upcomingTasks.isEmpty()) {
                    TintedPanel {
                        Text(
                            text = "No upcoming assignments are queued right now.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        upcomingTasks.forEach { task ->
                            ScheduleCompactCard(task = task)
                        }
                    }
                }
            }
        }

        item {
            TintedPanel {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "Daily digest preview".uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "A quick local summary of what matters after your immediate queue and before the next sync cycle lands.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ResponsiveFlowRow(maxItemsInEachRow = 3) {
                    ElevatedInfoCard(
                        title = "Due now",
                        value = dueTodayCount.toString(),
                        supportingText = "Items due today or within the next 24 hours",
                        modifier = Modifier.fillMaxWidth(),
                        accent = if (dueTodayCount > 0) Negative else SafeGreen
                    )
                    ElevatedInfoCard(
                        title = "Overdue",
                        value = overdueCount.toString(),
                        supportingText = "Work that needs recovery time immediately",
                        modifier = Modifier.fillMaxWidth(),
                        accent = if (overdueCount > 0) Negative else SafeGreen
                    )
                    ElevatedInfoCard(
                        title = "Assessments",
                        value = upcomingAssessments.toString(),
                        supportingText = "Quizzes and exams still ahead of you",
                        modifier = Modifier.fillMaxWidth(),
                        accent = MaterialTheme.colorScheme.primary
                    )
                }
                if (!digestHeadline.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = digestHeadline,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            TintedPanel {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text(
                        text = "Operations".uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Last sync ${syncState.lastSyncMillis.formatDateTime()} • ${eventState.recentEvents.size} recent changes captured locally",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${taskState.tasks.count { !it.isCompleted }} open items",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    LiquidGlassTextButton(text = "Activity log", onClick = onNavigateToActivity, modifier = Modifier.weight(1f), showArrow = true)
                    LiquidGlassTextButton(text = "Study planner", onClick = onNavigateToStudyPlanner, modifier = Modifier.weight(1f), showArrow = true)
                }
                LiquidGlassTextButton(text = "Exam focus mode", onClick = onNavigateToExamMode, modifier = Modifier.fillMaxWidth(), showArrow = true)
                if (!authState.isSignedIn) {
                    LiquidGlassTextButton(
                        text = "Connect Google account",
                        onClick = onNavigateToAuth,
                        modifier = Modifier.fillMaxWidth(),
                        showArrow = true
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleFeatureCard(
    task: AcademicTask?,
    fallbackTitle: String,
    fallbackSubtitle: String
) {
    if (task == null) {
        TintedPanel {
            Text(text = fallbackTitle, style = MaterialTheme.typography.titleMedium)
            Text(
                text = fallbackSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val tone = task.deadlineTone()
    TintedPanel(accentColor = tone.color) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = task.courseName,
                    style = MaterialTheme.typography.labelLarge,
                    color = tone.color
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatTimeWindow(task.dueDate),
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = tone.color.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tone.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = tone.color
                )
            }
        }
    }
}

@Composable
private fun ScheduleCompactCard(task: AcademicTask) {
    val tone = task.deadlineTone()
    TintedPanel(accentColor = tone.color) {
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
                        .width(4.dp)
                        .background(tone.color, RoundedCornerShape(99.dp))
                        .padding(vertical = 18.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = task.courseName,
                        style = MaterialTheme.typography.labelLarge,
                        color = tone.color
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = (task.dueDate ?: 0L).let { if (it == 0L) "No time" else homeTimeFormatter.format(Date(it)) },
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                color = tone.color
            )
        }
    }
}

private fun formatTimeWindow(dueDate: Long?): String {
    if (dueDate == null) return "No time assigned"
    return homeTimeFormatter.format(Date(dueDate))
}
