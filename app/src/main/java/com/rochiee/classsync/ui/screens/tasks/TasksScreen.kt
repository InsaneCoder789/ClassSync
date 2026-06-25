package com.rochiee.classsync.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.task.TaskEvent
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.ui.components.CourseChip
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.DeadlineText
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineTone
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun TasksScreen(
    taskState: TaskState,
    syncState: SyncState,
    onTaskEvent: (TaskEvent) -> Unit,
    onSyncEvent: (com.rochiee.classsync.bloc.sync.SyncEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        ScreenSection(title = "Tasks", subtitle = "Assignments, reminders, and manually added work.") {
            syncState.errorMessage?.let { error ->
                TintedPanel {
                    Text(text = error, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                }
            }
            Text(
                text = "Sync actions have moved to Settings so this space stays focused on actual work.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (taskState.tasks.isEmpty()) {
            EmptyState("No tasks yet", "Use sync or debug tools to bring in your academic tasks.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(taskState.tasks, key = { it.id }) { task ->
                    val tone = task.deadlineTone()
                    TintedPanel(accentColor = tone.color) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                                ResponsiveFlowRow(maxItemsInEachRow = 2) {
                                    CourseChip(courseName = task.courseName)
                                    DeadlineChip(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                                }
                                if (task.description.isNotBlank()) {
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 4
                                    )
                                }
                                DeadlineText(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                            }
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { checked ->
                                    onTaskEvent(TaskEvent.ToggleTaskCompletion(task.id, checked))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
