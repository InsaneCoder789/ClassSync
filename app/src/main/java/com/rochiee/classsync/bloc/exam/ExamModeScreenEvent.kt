package com.rochiee.classsync.bloc.exam

sealed class ExamModeScreenEvent {
    object LoadExamMode : ExamModeScreenEvent()
    data class ToggleChecklistItem(val eventId: String, val itemLabel: String) : ExamModeScreenEvent()
    object ClearError : ExamModeScreenEvent()
}
