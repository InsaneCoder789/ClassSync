package com.rochiee.classsync.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.bloc.task.TaskEvent
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.ui.components.CourseChip
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.DeadlineText
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineTone
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SafeGreen
import com.rochiee.classsync.ui.theme.SilverBorder
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
    val openTasks = remember(taskState.tasks) { taskState.tasks.filterNot { it.isCompleted } }
    val completedTasks = remember(taskState.tasks) { taskState.tasks.filter { it.isCompleted } }
    var showComposer by remember { mutableStateOf(false) }
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
            TintedPanel {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    SilverBorder.copy(alpha = 0.18f)
                                )
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "work desk",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Tasks",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Assignments, reminders, and your own manual reference tasks live together here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                syncState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    TaskStatCard(
                        title = "Open",
                        value = openTasks.size.toString(),
                        supporting = "Still waiting on you",
                        toneColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TaskStatCard(
                        title = "Completed",
                        value = completedTasks.size.toString(),
                        supporting = "Already checked off",
                        toneColor = if (completedTasks.isNotEmpty()) SafeGreen else Negative,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                LiquidGlassTextButton(
                    text = if (showComposer) "Hide task composer" else "Add a task",
                    onClick = {
                        showComposer = !showComposer
                        if (!showComposer) {
                            localError = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    selected = showComposer
                )
            }
        }

        if (showComposer) {
            item {
                TintedPanel {
                    Text(
                        text = "New task details",
                        style = MaterialTheme.typography.titleMedium
                    )
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
                                        showComposer = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            selected = true
                        )
                        LiquidGlassTextButton(
                            text = "Clear fields",
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

        item {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = "Your tasks",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "The newest and most urgent work stays closest to the top.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (taskState.tasks.isEmpty()) {
            item {
                EmptyState("No tasks yet", "Create your own task here or let sync bring in live coursework on the next refresh.")
            }
        } else {
            items(taskState.tasks.sortedWith(compareBy({ it.isCompleted }, { it.dueDate ?: Long.MAX_VALUE })), key = { it.id }) { task ->
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

@Composable
private fun TaskStatCard(
    title: String,
    value: String,
    supporting: String,
    toneColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    TintedPanel(modifier = modifier, accentColor = toneColor) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = toneColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = supporting,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun parseTaskDueDate(raw: String): Long? {
    val value = raw.trim()
    if (value.isEmpty()) return null
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(value)?.time
    }.getOrNull()
}
