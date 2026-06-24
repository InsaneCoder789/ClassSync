package com.rochiee.classsync.domain.usecase.exam

import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.exam.ExamModeAggregator
import com.rochiee.classsync.exam.ExamModeState

class GetExamModeUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val examModeAggregator: ExamModeAggregator
) {
    suspend operator fun invoke(): ExamModeState {
        return examModeAggregator.build(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot()
        )
    }
}
