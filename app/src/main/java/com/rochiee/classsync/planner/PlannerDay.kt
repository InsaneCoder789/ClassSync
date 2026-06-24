package com.rochiee.classsync.planner

data class PlannerDay(
    val dateStartMillis: Long,
    val dateEndMillis: Long,
    val tasks: List<PlannerItem>,
    val events: List<PlannerItem>,
    val dueItems: List<PlannerItem>,
    val highPriorityItems: List<PlannerItem>
)
