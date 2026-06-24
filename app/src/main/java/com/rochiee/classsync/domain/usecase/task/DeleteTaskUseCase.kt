package com.rochiee.classsync.domain.usecase.task

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: AcademicTask) {
        repository.deleteTask(task)
    }
}
