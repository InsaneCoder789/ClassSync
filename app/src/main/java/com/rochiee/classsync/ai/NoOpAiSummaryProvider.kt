package com.rochiee.classsync.ai

class NoOpAiSummaryProvider : AiSummaryProvider {
    override suspend fun summarize(title: String, content: String): AiSummaryResult {
        return AiSummaryResult(
            isAvailable = false,
            shortSummary = "AI summary unavailable",
            actionItems = emptyList(),
            deadlineHints = emptyList(),
            importanceLevel = "Unknown",
            errorMessage = "No AI provider is configured. Core ClassSync features continue to work without AI."
        )
    }
}
