package com.rochiee.classsync.bloc.event

import com.rochiee.classsync.domain.model.ClassroomEventType

sealed class EventEvent {
    object LoadEvents : EventEvent()
    data class LoadEventsByType(val type: ClassroomEventType) : EventEvent()
    object LoadRecentEvents : EventEvent()
    data class DeleteEvent(val eventId: String) : EventEvent()
    data class ConvertEventToTask(val eventId: String) : EventEvent()
    object ClearError : EventEvent()
}
