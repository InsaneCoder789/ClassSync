package com.rochiee.classsync.bloc.planner

import com.rochiee.classsync.planner.PlannerFilter

sealed class PlannerEvent {
    object LoadToday : PlannerEvent()
    object LoadCurrentWeek : PlannerEvent()
    object LoadCurrentMonth : PlannerEvent()
    data class LoadRange(val startMillis: Long, val endMillis: Long) : PlannerEvent()
    data class SetFilter(val filter: PlannerFilter) : PlannerEvent()
    object ClearError : PlannerEvent()
}
