package com.rochiee.classsync.ai

data class AiSummaryResult(
    val isAvailable: Boolean,
    val shortSummary: String,
    val actionItems: List<String>,
    val deadlineHints: List<String>,
    val importanceLevel: String,
    val errorMessage: String? = null
)
