package com.rochiee.classsync.bloc.task

import com.rochiee.classsync.domain.model.AcademicTask

sealed class TaskEvent {
    object LoadTasks : TaskEvent()
    data class AddTaskFromRawText(val rawText: String, val courseName: String) : TaskEvent()
    data class AddManualTask(
        val title: String,
        val description: String,
        val courseName: String,
        val dueDateMillis: Long?
    ) : TaskEvent()
    data class ToggleTaskCompletion(val taskId: Int, val isCompleted: Boolean) : TaskEvent()
    data class DeleteTask(val task: AcademicTask) : TaskEvent()
    object SyncGmailTasks : TaskEvent()
    object SyncClassroomTasks : TaskEvent()
    
    // Milestone 6: WorkManager Events
    object ScheduleBackgroundSync : TaskEvent()
    object CancelBackgroundSync : TaskEvent()
    object RunOneTimeFullSync : TaskEvent()
    object ExportTasksCsv : TaskEvent()
    object ExportTasksJson : TaskEvent()
}
