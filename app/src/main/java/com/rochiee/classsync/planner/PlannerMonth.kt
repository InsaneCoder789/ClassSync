package com.rochiee.classsync.planner

data class PlannerMonth(
    val monthStartMillis: Long,
    val monthEndMillis: Long,
    val weeks: List<PlannerWeek>,
    val totalTaskCount: Int,
    val completedTaskCount: Int,
    val overdueTaskCount: Int,
    val announcementCount: Int,
    val materialCount: Int,
    val quizExamCount: Int
)
