package com.rochiee.classsync.exam

data class ExamFocusItem(
    val eventId: String,
    val title: String,
    val courseName: String,
    val daysLeft: Int,
    val relatedMaterialTitles: List<String>,
    val pendingTaskTitles: List<String>,
    val countdownLabel: String,
    val checklist: List<String>
)
