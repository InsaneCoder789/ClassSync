package com.rochiee.classsync.dashboard

data class CourseDashboardSummary(
    val courseId: String,
    val courseName: String,
    val teacherName: String?,
    val pendingTaskCount: Int,
    val overdueCount: Int,
    val quizExamCount: Int,
    val announcementCount: Int,
    val materialCount: Int,
    val recentActivityCount: Int,
    val completedTaskCount: Int
)
