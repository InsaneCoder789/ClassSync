package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class ObserveEventsByTypeUseCase(
    private val repository: ClassroomEventRepository
) {
    operator fun invoke(type: ClassroomEventType) = repository.observeEventsByType(type)
}
