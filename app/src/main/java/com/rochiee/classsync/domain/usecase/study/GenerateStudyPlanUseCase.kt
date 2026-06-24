package com.rochiee.classsync.domain.usecase.study

import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.study.StudyPlan
import com.rochiee.classsync.study.StudyPlanGenerator

class GenerateStudyPlanUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val studyPlanGenerator: StudyPlanGenerator
) {
    suspend operator fun invoke(): StudyPlan {
        return studyPlanGenerator.generate(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot()
        )
    }
}
