package com.rochiee.classsync.ui.screens.tasks

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TasksScreen(
    taskState: TaskState,
    syncState: SyncState,
    onTaskEvent: (TaskEvent) -> Unit,
    onSyncEvent: (com.rochiee.classsync.bloc.sync.SyncEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val openTasks = remember(taskState.tasks) { taskState.tasks.filterNot { it.isCompleted } }
    val completedTasks = remember(taskState.tasks) { taskState.tasks.filter { it.isCompleted } }
    val isDarkPalette = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val taskPrimaryText = if (isDarkPalette) Color.White else MaterialTheme.colorScheme.onSurface
    val taskSecondaryText = if (isDarkPalette) Color.White.copy(alpha = 0.82f) else MaterialTheme.colorScheme.onSurfaceVariant
    val taskMutedText = if (isDarkPalette) Color.White.copy(alpha = 0.72f) else MaterialTheme.colorScheme.onSurfaceVariant
    var showComposer by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pickedDueDateMillis by remember { mutableStateOf<Long?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showClearCompletedDialog by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = pickedDueDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = selectedDate
                            }
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    calendar.set(Calendar.MINUTE, minute)
                                    calendar.set(Calendar.SECOND, 0)
                                    calendar.set(Calendar.MILLISECOND, 0)
                                    pickedDueDateMillis = calendar.timeInMillis
                                },
                                9,
                                0,
                                false
                            ).show()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pick time")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }

    if (showClearCompletedDialog) {
        AlertDialog(
            onDismissRequest = { showClearCompletedDialog = false },
            title = { Text("Clear completed tasks") },
            text = { Text("Remove all completed tasks from your directory? This keeps active work untouched.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        completedTasks.forEach { task ->
                            onTaskEvent(TaskEvent.DeleteTask(task))
                        }
                        showClearCompletedDialog = false
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCompletedDialog = false }) {
                    Text("Keep")
                }
            }
        )
    }

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
                        text = "WORK DESK",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    TaskStatCard(
                        title = "Open",
                        value = openTasks.size.toString(),
                        supporting = "Still waiting on you",
                        toneColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    TaskStatCard(
                        title = "Completed",
                        value = completedTasks.size.toString(),
                        supporting = "Already checked off",
                        toneColor = if (completedTasks.isNotEmpty()) SafeGreen else Negative,
                        modifier = Modifier.weight(1f)
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
                if (completedTasks.isNotEmpty()) {
                    LiquidGlassTextButton(
                        text = "Clean completed tasks",
                        onClick = { showClearCompletedDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
                        value = pickedDueDateMillis.formatComposerDueDate(),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Due date") },
                        singleLine = true,
                        readOnly = true
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
                            text = if (pickedDueDateMillis == null) "Open calendar" else "Change due date",
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                        LiquidGlassTextButton(
                            text = "Clear due date",
                            onClick = { pickedDueDateMillis = null },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = pickedDueDateMillis != null
                        )
                        LiquidGlassTextButton(
                            text = "Create task",
                            onClick = {
                                when {
                                    title.isBlank() -> localError = "Add a task title before saving."
                                    course.isBlank() -> localError = "Add a course or category so the task is easier to sort."
                                    else -> {
                                        onTaskEvent(
                                            TaskEvent.AddManualTask(
                                                title = title.trim(),
                                                description = description.trim(),
                                                courseName = course.trim(),
                                                dueDateMillis = pickedDueDateMillis
                                            )
                                        )
                                        title = ""
                                        course = ""
                                        description = ""
                                        pickedDueDateMillis = null
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
                                pickedDueDateMillis = null
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
                val displayTitle = remember(task.title) { sanitizeTaskTitle(task.title) }
                val displayDescription = remember(task.description) { sanitizeTaskDescription(task.description) }
                val postedTimestamp = remember(task.createdAtMillis) { formatPostedTimestamp(task.createdAtMillis) }
                TintedPanel(accentColor = tone.color) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = taskPrimaryText
                        )
                        ResponsiveFlowRow(maxItemsInEachRow = 2) {
                            CourseChip(courseName = task.courseName)
                            DeadlineChip(dueMillis = task.dueDate, isCompleted = task.isCompleted)
                        }
                        if (displayDescription.isNotBlank()) {
                            Text(
                                text = displayDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 4,
                                color = taskSecondaryText
                            )
                        }
                        if (task.dueDate != null) {
                            DeadlineText(
                                dueMillis = task.dueDate,
                                isCompleted = task.isCompleted,
                                colorOverride = if (isDarkPalette) Color.White else null
                            )
                        }
                        Text(
                            text = postedTimestamp,
                            style = MaterialTheme.typography.bodySmall,
                            color = taskMutedText
                        )
                        ResponsiveFlowRow(maxItemsInEachRow = 2) {
                            LiquidGlassTextButton(
                                text = if (task.isCompleted) "Undo completion" else "Complete task",
                                onClick = {
                                    onTaskEvent(
                                        TaskEvent.ToggleTaskCompletion(
                                            task.id,
                                            !task.isCompleted
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                selected = task.isCompleted
                            )
                            LiquidGlassTextButton(
                                text = if (task.isCompleted) "Remove task" else "Redirect",
                                onClick = {
                                    if (task.isCompleted) {
                                        onTaskEvent(TaskEvent.DeleteTask(task))
                                    } else {
                                        task.sourceLink?.takeIf { it.isNotBlank() }?.let(uriHandler::openUri)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = if (task.isCompleted) true else !task.sourceLink.isNullOrBlank()
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

private fun Long?.formatComposerDueDate(): String {
    if (this == null) return "Not set"
    return SimpleDateFormat("EEE, dd MMM yyyy • h:mm a", Locale.getDefault()).format(Date(this))
}

private fun sanitizeTaskTitle(raw: String): String {
    val trimmed = raw.trim()
    return trimmed
        .removePrefix("New Assignment:")
        .removePrefix("New assignment:")
        .trim()
        .ifBlank { "Untitled task" }
}

private fun sanitizeTaskDescription(raw: String): String {
    if (raw.isBlank()) return ""
    val ignoredPhrases = listOf(
        "notification settings",
        "google llc",
        "accounts.google.com",
        "accountchooser",
        "continue=https://classroom.google.com",
        "gmail.com"
    )
    return raw
        .lineSequence()
        .map(String::trim)
        .filter { line ->
            line.isNotBlank() && ignoredPhrases.none { ignored -> line.lowercase().contains(ignored) }
        }
        .map { line ->
            line.removePrefix("New Assignment:")
                .removePrefix("New assignment:")
                .trim()
        }
        .take(3)
        .joinToString(" ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

private fun formatPostedTimestamp(createdAtMillis: Long): String {
    if (createdAtMillis <= 0L) return "Posted recently"
    return runCatching {
        val formatter = SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault())
        "Posted ${formatter.format(Date(createdAtMillis))}"
    }.getOrElse { "Posted recently" }
}
