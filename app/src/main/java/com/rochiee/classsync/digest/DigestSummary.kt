package com.rochiee.classsync.digest

data class DigestSummary(
    val dueTodayCount: Int,
    val overdueCount: Int,
    val upcomingQuizExamCount: Int,
    val latestAnnouncementTitles: List<String>,
    val importantMaterialTitles: List<String>,
    val syncStatus: String,
    val generatedAtMillis: Long = System.currentTimeMillis()
)
