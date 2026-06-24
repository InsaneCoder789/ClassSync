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
import com.rochiee.classsync.ui.components.CourseChip
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.DeadlineText
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun TasksScreen(
    taskState: TaskState,
    onTaskEvent: (TaskEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        ScreenSection(title = "Tasks", subtitle = "Assignments, reminders, and manually added work.") {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(text = "Sync Classroom", onClick = { onTaskEvent(TaskEvent.SyncClassroomTasks) }, modifier = Modifier.weight(1f))
                LiquidGlassTextButton(text = "Sync Gmail", onClick = { onTaskEvent(TaskEvent.SyncGmailTasks) }, modifier = Modifier.weight(1f))
            }
        }

        if (taskState.tasks.isEmpty()) {
            EmptyState("No tasks yet", "Use sync or debug tools to bring in your academic tasks.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(taskState.tasks, key = { it.id }) { task ->
                    TintedPanel {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CourseChip(courseName = task.courseName)
                                    DeadlineChip(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                                }
                                if (task.description.isNotBlank()) {
                                    Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
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
