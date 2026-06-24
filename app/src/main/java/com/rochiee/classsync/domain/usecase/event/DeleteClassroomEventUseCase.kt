package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class DeleteClassroomEventUseCase(
    private val repository: ClassroomEventRepository
) {
    suspend operator fun invoke(eventId: String) {
        repository.deleteEvent(eventId)
    }
}
