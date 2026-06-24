package com.rochiee.classsync.taskengine

import com.rochiee.classsync.data.remote.gmail.GmailMessageDto
import com.rochiee.classsync.domain.model.AcademicTask

object GmailTaskParser {
    private val keywords = listOf(
        "assignment", "due", "submit", "homework", "coursework",
        "quiz", "exam", "deadline", "posted", "upload", "turn in"
    )

    fun parse(message: GmailMessageDto): AcademicTask? {
        val subject = message.subject ?: ""
        val snippet = message.snippet ?: ""
        val body = message.body ?: ""
        val fullContent = "$subject $snippet $body"
        val normalizedContent = fullContent.lowercase()

        // Check if academic
        val isAcademic = keywords.any { normalizedContent.contains(it) }
        if (!isAcademic) return null

        // Ignore non-academic common emails
        val ignoreKeywords = listOf("login", "otp", "promotion", "spam", "social")
        if (ignoreKeywords.any { normalizedContent.contains(it) }) return null

        val title = subject.ifBlank { snippet.take(50) }
        val courseName = if (message.from?.contains("classroom.google.com") == true) {
            subject.split(":").firstOrNull()?.trim() ?: "Unknown Course"
        } else {
            "Academic"
        }
        val dueDate = DeadlineParser.parse(fullContent)

        return AcademicTask(
            title = title,
            description = snippet,
            courseName = courseName,
            isCompleted = false,
            dueDate = dueDate,
            priority = 2,
            source = "Gmail",
            sourceId = message.threadId.ifBlank { message.id },
            sourceLink = message.link,
            createdAtMillis = message.internalDateMillis,
            updatedAtMillis = message.internalDateMillis
        )
    }
}
