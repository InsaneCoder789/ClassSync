package com.rochiee.classsync.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.event.EventState
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun

@Composable
fun HomeScreen(
    authState: AuthUiState,
    taskState: TaskState,
    syncState: SyncState,
    eventState: EventState,
    onNavigateToActivity: () -> Unit,
    onNavigateToStudyPlanner: () -> Unit,
    onNavigateToExamMode: () -> Unit,
    onNavigateToDebug: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val spacing = LocalSpacing.current
    val pendingTasks = taskState.tasks.count { !it.isCompleted }
    val overdueTasks = taskState.tasks.count { !it.isCompleted && (it.dueDate ?: Long.MAX_VALUE) < System.currentTimeMillis() }
    val nextTask = taskState.tasks
        .filter { !it.isCompleted && it.dueDate != null }
        .minByOrNull { it.dueDate ?: Long.MAX_VALUE }

    Column(
        modifier = Modifier
            .padding(horizontal = spacing.md, vertical = spacing.sm)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        ScreenSection(
            title = "Academic home",
            subtitle = if (authState.isSignedIn) {
                "Signed in as ${authState.userEmail ?: authState.displayName ?: "student"}"
            } else {
                "Connect Google to unlock Classroom and optional Gmail sync."
            }
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), modifier = Modifier.fillMaxWidth()) {
                ElevatedInfoCard(
                    title = "Today",
                    value = pendingTasks.toString(),
                    supportingText = "Pending tasks in your local workspace",
                    modifier = Modifier.weight(1f),
                    accent = SkyBlue
                )
                ElevatedInfoCard(
                    title = "Urgent",
                    value = overdueTasks.toString(),
                    supportingText = "Tasks already past due",
                    modifier = Modifier.weight(1f),
                    accent = Sun
                )
            }
        }

        ScreenSection(title = "Classroom pulse", subtitle = "Announcements, materials, and assessments collected so far.") {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), modifier = Modifier.fillMaxWidth()) {
                ElevatedInfoCard(
                    title = "Announcements",
                    value = eventState.announcements.size.toString(),
                    supportingText = "Course updates and notices",
                    modifier = Modifier.weight(1f),
                    accent = MintGreen
                )
                ElevatedInfoCard(
                    title = "Quizzes",
                    value = (eventState.quizzes.size + eventState.exams.size).toString(),
                    supportingText = "Assessments currently tracked",
                    modifier = Modifier.weight(1f),
                    accent = SkyBlue
                )
            }
        }

        ScreenSection(title = "Sync status") {
            TintedPanel {
                Text(
                    text = "Last sync: ${syncState.lastSyncMillis.formatDateTime()}",
                    style = MaterialTheme.typography.titleMedium
                )
                nextTask?.let { task ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Next deadline: ${task.title}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        ResponsiveFlowRow(maxItemsInEachRow = 2) {
                            DeadlineChip(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                            Text(
                                text = task.dueDate.formatDateTime(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Text(
                    text = "Recent logs: ${syncState.logs.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (authState.isSignedIn) {
                        "Manual sync controls now live in Settings for a cleaner home view."
                    } else {
                        "Connect Google from Settings to start Classroom sync."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
