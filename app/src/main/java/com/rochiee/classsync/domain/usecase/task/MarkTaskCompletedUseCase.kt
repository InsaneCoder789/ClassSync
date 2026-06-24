package com.rochiee.classsync.domain.usecase.task

import com.rochiee.classsync.domain.repository.TaskRepository

class MarkTaskCompletedUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Int, isCompleted: Boolean) {
        val task = repository.getTaskById(taskId)
        task?.let {
            repository.updateTask(it.copy(isCompleted = isCompleted))
        }
    }
}