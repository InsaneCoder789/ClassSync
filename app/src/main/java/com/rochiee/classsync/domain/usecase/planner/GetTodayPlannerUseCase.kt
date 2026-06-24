package com.rochiee.classsync.domain.usecase.planner

import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.planner.PlannerAggregator
import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.planner.PlannerFilter

class GetTodayPlannerUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val plannerAggregator: PlannerAggregator
) {
    suspend operator fun invoke(filter: PlannerFilter = PlannerFilter()): PlannerDay {
        return plannerAggregator.buildTodayPlanner(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot(),
            filter = filter
        )
    }
}
