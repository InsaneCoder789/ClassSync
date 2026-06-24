package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class ObserveAllEventsUseCase(
    private val repository: ClassroomEventRepository
) {
    operator fun invoke() = repository.observeAllEvents()
}
