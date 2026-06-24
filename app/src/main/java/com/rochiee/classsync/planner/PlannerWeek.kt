package com.rochiee.classsync.planner

data class PlannerWeek(
    val weekStartMillis: Long,
    val weekEndMillis: Long,
    val days: List<PlannerDay>,
    val totalTaskCount: Int,
    val completedTaskCount: Int,
    val overdueTaskCount: Int,
    val quizExamCount: Int
)
