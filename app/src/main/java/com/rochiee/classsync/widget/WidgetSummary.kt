package com.rochiee.classsync.widget

data class WidgetSummary(
    val todayTaskCount: Int,
    val urgentTaskCount: Int,
    val overdueTaskCount: Int,
    val nextTaskTitle: String?,
    val nextTaskCourseName: String?,
    val nextTaskDueMillis: Long?,
    val secondTaskTitle: String?,
    val secondTaskDueMillis: Long?,
    val lastUpdatedMillis: Long
)
