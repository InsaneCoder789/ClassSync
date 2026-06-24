package com.rochiee.classsync.domain.usecase.task

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<AcademicTask>> {
        return repository.observeTasks()
    }
}