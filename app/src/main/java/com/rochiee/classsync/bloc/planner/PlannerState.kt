package com.rochiee.classsync.bloc.planner

import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.planner.PlannerFilter
import com.rochiee.classsync.planner.PlannerMonth
import com.rochiee.classsync.planner.PlannerWeek

data class PlannerState(
    val isLoading: Boolean = false,
    val today: PlannerDay? = null,
    val currentWeek: PlannerWeek? = null,
    val currentMonth: PlannerMonth? = null,
    val selectedRangeDays: List<PlannerDay> = emptyList(),
    val selectedRangeStartMillis: Long? = null,
    val selectedRangeEndMillis: Long? = null,
    val activeFilter: PlannerFilter = PlannerFilter(),
    val errorMessage: String? = null,
    val lastUpdatedMillis: Long? = null
)
