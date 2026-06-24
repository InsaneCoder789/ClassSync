package com.rochiee.classsync.bloc.study

sealed class StudyPlanEvent {
    object GeneratePlan : StudyPlanEvent()
    data class ToggleBlockDone(val itemId: String) : StudyPlanEvent()
    object ClearError : StudyPlanEvent()
}
