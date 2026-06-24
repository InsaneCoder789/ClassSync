package com.rochiee.classsync.domain.usecase.export

import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.export.TaskExportManager
import java.io.File

class ExportTasksJsonUseCase(
    private val taskRepository: TaskRepository,
    private val taskExportManager: TaskExportManager
) {
    suspend operator fun invoke(): File {
        return taskExportManager.exportTasksJson(taskRepository.getTasksSnapshot())
    }
}
