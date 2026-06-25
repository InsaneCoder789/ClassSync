package com.rochiee.classsync.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.event.EventState
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineTone
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val homeTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

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
    val ongoingTask = openTasks.firstOrNull { task ->
        val due = task.dueDate ?: return@firstOrNull false
        due in now..(now + 2L * 60L * 60L * 1000L)
    } ?: openTasks.firstOrNull()
    val upcomingTasks = openTasks.filterNot { it.id == ongoingTask?.id }.take(3)

    Column(
        modifier = Modifier
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
            .padding(horizontal = spacing.md, vertical = spacing.sm)
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
                text = if (authState.isSignedIn) {
                    "Working as ${authState.userEmail ?: authState.displayName ?: "student"}"
                } else {
                    "Connect Google from settings to unlock live Classroom work"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Ongoing",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ScheduleFeatureCard(
                task = ongoingTask,
                fallbackTitle = "No live assignment right now",
                fallbackSubtitle = "Your next timed work will appear here once Classroom dates are synced."
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "Upcoming",
                style = MaterialTheme.typography.titleMedium,
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

        TintedPanel {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = "Operations",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Last sync ${syncState.lastSyncMillis.formatDateTime()} • ${eventState.recentEvents.size} recent changes",
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
                        text = "${taskState.tasks.count { !it.isCompleted }} open",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(text = "Activity", onClick = onNavigateToActivity, modifier = Modifier.weight(1f))
                LiquidGlassTextButton(text = "Study", onClick = onNavigateToStudyPlanner, modifier = Modifier.weight(1f))
            }
            LiquidGlassTextButton(text = "Exam", onClick = onNavigateToExamMode, modifier = Modifier.fillMaxWidth())
            if (!authState.isSignedIn) {
                LiquidGlassTextButton(
                    text = "Connect Google",
                    onClick = onNavigateToAuth,
                    modifier = Modifier.fillMaxWidth()
                )
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
                    style = MaterialTheme.typography.titleMedium,
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
                        style = MaterialTheme.typography.titleMedium
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
