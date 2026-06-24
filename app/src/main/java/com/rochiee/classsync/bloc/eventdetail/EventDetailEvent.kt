package com.rochiee.classsync.bloc.eventdetail

sealed class EventDetailEvent {
    data class LoadEvent(val eventId: String) : EventDetailEvent()
    data class SummarizeEvent(val eventId: String) : EventDetailEvent()
    object ClearError : EventDetailEvent()
}
