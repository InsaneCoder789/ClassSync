package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class SaveClassroomEventUseCase(
    private val repository: ClassroomEventRepository
) {
    suspend operator fun invoke(event: ClassroomEvent) {
        repository.saveEvent(event)
    }
}
