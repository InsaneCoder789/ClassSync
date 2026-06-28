package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.TaskPriority
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.ml.classifier.EventClassifierInput
import com.rochiee.classsync.ml.classifier.HybridEventClassifier
import com.rochiee.classsync.taskengine.DeadlineParser
import kotlinx.coroutines.flow.first

class ClassroomEventParser(
    private val settingsRepository: SettingsRepository,
    private val hybridEventClassifier: HybridEventClassifier
) {
    suspend fun parse(input: RawClassroomEventInput): ClassroomEvent? {
        val title = input.title?.trim().orEmpty()
        val body = input.body?.trim().orEmpty()
        val originalText = listOf(title, body).filter { it.isNotBlank() }.joinToString("\n").trim()
        if (originalText.isBlank()) return null

        val normalizedText = cleanText(originalText)
        val dueDate = input.dueDateMillisOverride ?: DeadlineParser.parse(originalText, input.receivedAtMillis)
        val eventId = ClassroomEventFingerprintGenerator.generate(input, normalizedText)
        val settings = settingsRepository.observeSettings().first()
        val classification = hybridEventClassifier.classify(
            input = EventClassifierInput(
                title = title.ifBlank { null },
                body = body.ifBlank { null },
                courseName = input.courseName,
                source = input.source,
                dueDateMillis = dueDate
            ),
            smartClassificationEnabled = settings.smartClassificationEnabled,
            tfliteClassificationEnabled = settings.tfliteClassificationEnabled,
            createTasksFromActionableNoDateAnnouncements =
                settings.createTasksFromActionableNoDateAnnouncements
        )
        val heuristicPriority = EventPriorityEngine.priorityFor(
            classification.eventType,
            classification.actionType,
            dueDate,
            input.receivedAtMillis
        )
        val priority = maxPriority(classification.priority, heuristicPriority)

        return ClassroomEvent(
            id = eventId,
            title = if (title.isNotBlank()) title else normalizedText.take(80),
            description = body.ifBlank { null },
            courseId = input.courseId,
            courseName = input.courseName,
            eventType = classification.eventType,
            actionType = classification.actionType,
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

    private fun maxPriority(first: TaskPriority, second: TaskPriority): TaskPriority {
        return if (first.score >= second.score) first else second
    }
}
