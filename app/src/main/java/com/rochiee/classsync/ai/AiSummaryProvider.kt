package com.rochiee.classsync.ai

interface AiSummaryProvider {
    suspend fun summarize(title: String, content: String): AiSummaryResult
}
