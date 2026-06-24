package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.taskengine.DeadlineParser

class ClassroomEventParser {
    fun parse(input: RawClassroomEventInput): ClassroomEvent? {
        val title = input.title?.trim().orEmpty()
        val body = input.body?.trim().orEmpty()
        val originalText = listOf(title, body).filter { it.isNotBlank() }.joinToString("\n").trim()
        if (originalText.isBlank()) return null

        val normalizedText = cleanText(originalText)
        val eventType = ClassroomEventClassifier.classify(normalizedText)
        val actionType = ClassroomEventClassifier.actionTypeFor(eventType)
        val dueDate = DeadlineParser.parse(originalText, input.receivedAtMillis)
        val eventId = ClassroomEventFingerprintGenerator.generate(input, normalizedText)
        val priority = EventPriorityEngine.priorityFor(eventType, actionType, dueDate, input.receivedAtMillis)

        return ClassroomEvent(
            id = eventId,
            title = if (title.isNotBlank()) title else normalizedText.take(80),
            description = body.ifBlank { null },
            courseId = input.courseId,
            courseName = input.courseName,
            eventType = eventType,
            actionType = actionType,
            source = input.source,
            sourceId = input.sourceId,
            eventTimeMillis = input.receivedAtMillis,
            dueDateMillis = dueDate,
            priority = priority,
            originalText = originalText,
            originalLink = input.originalLink,
            convertedToTask = false,
            createdAtMillis = input.receivedAtMillis,
            updatedAtMillis = input.receivedAtMillis
        )
    }

    private fun cleanText(text: String): String {
        return text.replace(Regex("\\s+"), " ").trim()
    }
}
