package com.rochiee.classsync.bloc.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.usecase.export.ExportTasksCsvUseCase
import com.rochiee.classsync.domain.usecase.export.ExportTasksJsonUseCase
import com.rochiee.classsync.domain.usecase.gmail.SyncGmailTasksUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.synclog.AddSyncLogUseCase
import com.rochiee.classsync.domain.usecase.task.AddManualTaskUseCase
import com.rochiee.classsync.domain.usecase.task.DeleteTaskUseCase
import com.rochiee.classsync.domain.usecase.task.MarkTaskCompletedUseCase
import com.rochiee.classsync.domain.usecase.task.ObserveTasksUseCase
import com.rochiee.classsync.domain.usecase.worker.CancelBackgroundSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.RunOneTimeFullSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.ScheduleBackgroundSyncUseCase
import com.rochiee.classsync.taskengine.DeadlineParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskBlocViewModel(
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val addManualTaskUseCase: AddManualTaskUseCase,
    private val markTaskCompletedUseCase: MarkTaskCompletedUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val syncGmailTasksUseCase: SyncGmailTasksUseCase,
    private val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase,
    private val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase,
    private val scheduleBackgroundSyncUseCase: ScheduleBackgroundSyncUseCase,
    private val cancelBackgroundSyncUseCase: CancelBackgroundSyncUseCase,
    private val runOneTimeFullSyncUseCase: RunOneTimeFullSyncUseCase,
    private val addSyncLogUseCase: AddSyncLogUseCase,
    private val exportTasksCsvUseCase: ExportTasksCsvUseCase,
    private val exportTasksJsonUseCase: ExportTasksJsonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        onEvent(TaskEvent.LoadTasks)
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.LoadTasks -> {
                observeTasks()
            }
            is TaskEvent.AddTaskFromRawText -> {
                addTaskFromRawText(event.rawText, event.courseName)
            }
            is TaskEvent.AddManualTask -> {
                addManualTask(event.title, event.description, event.courseName, event.dueDateMillis)
            }
            is TaskEvent.ToggleTaskCompletion -> {
                toggleCompletion(event.taskId, event.isCompleted)
            }
            is TaskEvent.DeleteTask -> {
                deleteTask(event.task)
            }
            TaskEvent.SyncGmailTasks -> {
                syncGmailTasks()
            }
            TaskEvent.SyncClassroomTasks -> {
                syncClassroomTasks()
            }
            TaskEvent.ScheduleBackgroundSync -> {
                scheduleBackgroundSync()
            }
            TaskEvent.CancelBackgroundSync -> {
                cancelBackgroundSync()
            }
            TaskEvent.RunOneTimeFullSync -> {
                runOneTimeFullSync()
            }
            TaskEvent.ExportTasksCsv -> {
                exportTasksCsv()
            }
            TaskEvent.ExportTasksJson -> {
                exportTasksJson()
            }
        }
    }

    private fun observeTasks() {
        _state.update { it.copy(isLoading = true) }
        observeTasksUseCase()
            .onEach { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
            .catch { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun addTaskFromRawText(rawText: String, courseName: String) {
        val lines = rawText.lines()
        val title = lines.find { it.contains("posted:", ignoreCase = true) }
            ?.substringAfter("posted:")?.trim() ?: "Raw Task"
        
        viewModelScope.launch {
            addManualTaskUseCase(
                AcademicTask(
                    title = title,
                    description = rawText,
                    courseName = courseName,
                    dueDate = DeadlineParser.parse(rawText),
                    source = "Raw Text"
                )
            )
        }
    }

    private fun addManualTask(title: String, description: String, courseName: String, dueDateMillis: Long?) {
        viewModelScope.launch {
            addManualTaskUseCase(
                AcademicTask(
                    title = title,
                    description = description,
                    courseName = courseName,
                    dueDate = dueDateMillis,
                    source = "Manual"
                )
            )
        }
    }

    private fun toggleCompletion(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            markTaskCompletedUseCase(taskId, isCompleted)
        }
    }

    private fun deleteTask(task: AcademicTask) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    private fun syncGmailTasks() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                syncGmailTasksUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "GMAIL",
                        status = "SUCCESS",
                        message = "Synced Gmail tasks successfully.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun syncClassroomTasks() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Sync both courses and coursework to ensure everything is up to date
                syncClassroomCoursesUseCase()
                syncClassroomCourseworkUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "CLASSROOM",
                        status = "SUCCESS",
                        message = "Synced Classroom courses and coursework successfully.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun scheduleBackgroundSync() {
        viewModelScope.launch {
            try {
                scheduleBackgroundSyncUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "WORK_SCHEDULER",
                        status = "SUCCESS",
                        message = "Scheduled background sync jobs.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun cancelBackgroundSync() {
        viewModelScope.launch {
            try {
                cancelBackgroundSyncUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "WORK_SCHEDULER",
                        status = "SUCCESS",
                        message = "Cancelled background sync jobs.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun runOneTimeFullSync() {
        viewModelScope.launch {
            try {
                runOneTimeFullSyncUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "WORK_SCHEDULER",
                        status = "SUCCESS",
                        message = "Queued one-time full sync.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun exportTasksCsv() {
        viewModelScope.launch {
            try {
                val file = exportTasksCsvUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "EXPORT",
                        status = "SUCCESS",
                        message = "Exported CSV to ${file.absolutePath}",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun exportTasksJson() {
        viewModelScope.launch {
            try {
                val file = exportTasksJsonUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "EXPORT",
                        status = "SUCCESS",
                        message = "Exported JSON to ${file.absolutePath}",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }
}
