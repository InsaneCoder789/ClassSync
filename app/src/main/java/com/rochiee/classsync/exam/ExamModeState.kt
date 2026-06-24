package com.rochiee.classsync.exam

data class ExamModeState(
    val generatedAtMillis: Long,
    val upcomingExams: List<ExamFocusItem>
)
