package com.rochiee.classsync.study

data class StudyPlanItem(
    val id: String,
    val title: String,
    val courseName: String,
    val scheduledDateMillis: Long,
    val sourceType: String,
    val priorityExplanation: String,
    val estimatedEffortLabel: String,
    val notes: String = "",
    val isManual: Boolean = false,
    val isDone: Boolean = false
)
