package com.rochiee.classsync.ai

class AnnouncementSummarizer(
    private val aiSummaryProvider: AiSummaryProvider
) {
    suspend fun summarize(title: String, content: String): AiSummaryResult {
        return aiSummaryProvider.summarize(title, content)
    }
}
