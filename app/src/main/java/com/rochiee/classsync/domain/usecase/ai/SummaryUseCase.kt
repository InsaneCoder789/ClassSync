package com.rochiee.classsync.domain.usecase.ai

import com.rochiee.classsync.ai.AiSummaryResult
import com.rochiee.classsync.ai.AnnouncementSummarizer
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class SummaryUseCase(
    private val classroomEventRepository: ClassroomEventRepository,
    private val announcementSummarizer: AnnouncementSummarizer
) {
    suspend operator fun invoke(eventId: String): AiSummaryResult {
        val event = classroomEventRepository.getEventById(eventId)
            ?: return AiSummaryResult(
                isAvailable = false,
                shortSummary = "Summary unavailable",
                actionItems = emptyList(),
                deadlineHints = emptyList(),
                importanceLevel = "Unknown",
                errorMessage = "The selected event could not be found."
            )

        if (event.eventType != ClassroomEventType.ANNOUNCEMENT && event.eventType != ClassroomEventType.MATERIAL) {
            return AiSummaryResult(
                isAvailable = false,
                shortSummary = "Summary unavailable",
                actionItems = emptyList(),
                deadlineHints = emptyList(),
                importanceLevel = "Not applicable",
                errorMessage = "AI summaries are currently intended for announcements and materials."
            )
        }

        val content = listOfNotNull(event.description, event.originalText).joinToString("\n").trim()
        if (content.length < 80) {
            return AiSummaryResult(
                isAvailable = true,
                shortSummary = content.ifBlank { event.title },
                actionItems = emptyList(),
                deadlineHints = listOfNotNull(event.dueDateMillis?.let { "Due date attached" }),
                importanceLevel = event.priority.name
            )
        }

        return announcementSummarizer.summarize(event.title, content)
    }
}
