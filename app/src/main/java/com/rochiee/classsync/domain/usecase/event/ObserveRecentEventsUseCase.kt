package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class ObserveRecentEventsUseCase(
    private val repository: ClassroomEventRepository
) {
    operator fun invoke(limit: Int = 20) = repository.observeRecentEvents(limit)
}
