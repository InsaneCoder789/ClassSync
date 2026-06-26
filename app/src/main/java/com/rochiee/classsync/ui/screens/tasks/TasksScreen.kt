package com.rochiee.classsync.ui.screens.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.task.TaskEvent
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.ui.components.CourseChip
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.DeadlineText
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineTone
import com.rochiee.classsync.ui.theme.LocalSpacing
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TasksScreen(
    taskState: TaskState,
    syncState: SyncState,
    onTaskEvent: (TaskEvent) -> Unit,
    onSyncEvent: (com.rochiee.classsync.bloc.sync.SyncEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var title by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        item {
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
        }

        item {
            ScreenSection(title = "Add a task", subtitle = "Keep your own reference tasks next to synced coursework.") {
                TintedPanel {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Title") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Course or category") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dueInput,
                        onValueChange = { dueInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Due date (optional, yyyy-MM-dd HH:mm)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Notes") },
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
                            text = "Create task",
                            onClick = {
                                val parsedDueDate = parseTaskDueDate(dueInput)
                                when {
                                    title.isBlank() -> localError = "Add a task title before saving."
                                    course.isBlank() -> localError = "Add a course or category so the task is easier to sort."
                                    dueInput.isNotBlank() && parsedDueDate == null -> localError = "Use the due date format yyyy-MM-dd HH:mm."
                                    else -> {
                                        onTaskEvent(
                                            TaskEvent.AddManualTask(
                                                title = title.trim(),
                                                description = description.trim(),
                                                courseName = course.trim(),
                                                dueDateMillis = parsedDueDate
                                            )
                                        )
                                        title = ""
                                        course = ""
                                        description = ""
                                        dueInput = ""
                                        localError = null
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        LiquidGlassTextButton(
                            text = "Clear",
                            onClick = {
                                title = ""
                                course = ""
                                description = ""
                                dueInput = ""
                                localError = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (taskState.tasks.isEmpty()) {
            item {
                EmptyState("No tasks yet", "Create your own task here or let Classroom and Gmail fill this list after the next sync.")
            }
        } else {
            items(taskState.tasks, key = { it.id }) { task ->
                val tone = task.deadlineTone()
                TintedPanel(accentColor = tone.color) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
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
                            if (task.dueDate != null) {
                                DeadlineText(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                            }
                        }
                        Box(modifier = Modifier.padding(start = spacing.sm)) {
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

private fun parseTaskDueDate(raw: String): Long? {
    val value = raw.trim()
    if (value.isEmpty()) return null
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(value)?.time
    }.getOrNull()
}
