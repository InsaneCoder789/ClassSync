package com.rochiee.classsync.study

data class StudyPlan(
    val generatedAtMillis: Long,
    val items: List<StudyPlanItem>
)
