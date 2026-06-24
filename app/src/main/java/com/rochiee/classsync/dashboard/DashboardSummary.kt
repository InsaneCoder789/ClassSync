package com.rochiee.classsync.dashboard

data class DashboardSummary(
    val todayTaskCount: Int,
    val upcomingTaskCount: Int,
    val overdueTaskCount: Int,
    val announcementCount: Int,
    val materialCount: Int,
    val quizCount: Int,
    val examCount: Int,
    val recentEventCount: Int,
    val lastSyncMillis: Long?
)
