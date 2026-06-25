package com.rochiee.classsync.widget

data class WidgetSummary(
    val todayTaskCount: Int,
    val urgentTaskCount: Int,
    val overdueTaskCount: Int,
    val primaryTaskTitle: String?,
    val primaryTaskCourseName: String?,
    val primaryTaskDueMillis: Long?,
    val redZoneOverflowCount: Int
)
