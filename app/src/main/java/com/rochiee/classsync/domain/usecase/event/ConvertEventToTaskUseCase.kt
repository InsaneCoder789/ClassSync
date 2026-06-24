package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.eventengine.EventToTaskConverter

class ConvertEventToTaskUseCase(
    private val classroomEventRepository: ClassroomEventRepository,
    private val taskRepository: TaskRepository,
    private val eventToTaskConverter: EventToTaskConverter
) {
    suspend operator fun invoke(eventId: String): ClassroomEvent? {
        val event = classroomEventRepository.getEventById(eventId) ?: return null
        val task = eventToTaskConverter.convert(event)
        if (task != null) {
            taskRepository.addTask(task)
            classroomEventRepository.markConvertedToTask(eventId, true)
        }
        return event
    }
}
