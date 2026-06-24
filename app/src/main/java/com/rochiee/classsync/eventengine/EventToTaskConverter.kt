package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType

class EventToTaskConverter {
    fun convert(event: ClassroomEvent): AcademicTask? {
        if (!isActionable(event)) return null

        return AcademicTask(
            title = event.title,
            description = event.description ?: event.originalText.orEmpty(),
            courseName = event.courseName ?: "Unknown Course",
            isCompleted = false,
            dueDate = event.dueDateMillis,
            priority = event.priority.score,
            source = event.source.name.lowercase().replaceFirstChar { it.uppercase() },
            sourceId = event.sourceId ?: event.id,
            sourceLink = event.originalLink,
            createdAtMillis = event.createdAtMillis,
            updatedAtMillis = event.updatedAtMillis
        )
    }

    private fun isActionable(event: ClassroomEvent): Boolean {
        return when (event.eventType) {
            ClassroomEventType.ASSIGNMENT,
            ClassroomEventType.COURSEWORK,
            ClassroomEventType.QUIZ,
            ClassroomEventType.EXAM,
            ClassroomEventType.REMINDER -> true
            ClassroomEventType.MATERIAL -> {
                val text = listOf(event.title, event.description.orEmpty(), event.originalText.orEmpty())
                    .joinToString(" ")
                    .lowercase()
                listOf("read", "complete", "prepare", "revise", "before next class").any { text.contains(it) }
            }
            else -> false
        }
    }
}
