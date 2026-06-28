package com.rochiee.classsync.ml.classifier

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority

object ClassificationMapper {
    fun toResult(
        label: EventClassificationLabel,
        source: ClassificationSource,
        confidence: Float,
        reason: String,
        inputText: String,
        dueDateMillis: Long?,
        createTasksFromActionableNoDateAnnouncements: Boolean
    ): EventClassificationResult {
        val eventType = when (label) {
            EventClassificationLabel.DUE_DATE_TASK -> {
                if (inputText.containsAny("remind", "deadline", "due")) ClassroomEventType.REMINDER
                else ClassroomEventType.ASSIGNMENT
            }
            EventClassificationLabel.TASK_REQUIRED -> ClassroomEventType.COURSEWORK
            EventClassificationLabel.ACTIONABLE_NO_DATE -> {
                if (createTasksFromActionableNoDateAnnouncements) ClassroomEventType.ASSIGNMENT
                else ClassroomEventType.ANNOUNCEMENT
            }
            EventClassificationLabel.TEST_OR_EXAM_INFO -> {
                if (inputText.containsAny("quiz", "mcq")) ClassroomEventType.QUIZ else ClassroomEventType.EXAM
            }
            EventClassificationLabel.SUBMISSION_INSTRUCTION -> ClassroomEventType.ASSIGNMENT
            EventClassificationLabel.INFORMATION_ONLY -> ClassroomEventType.ANNOUNCEMENT
            EventClassificationLabel.ANNOUNCEMENT_ONLY -> ClassroomEventType.ANNOUNCEMENT
            EventClassificationLabel.MATERIAL_ONLY -> ClassroomEventType.MATERIAL
            EventClassificationLabel.GRADE_OR_FEEDBACK -> {
                if (inputText.containsAny("feedback", "comment", "reviewed", "returned")) {
                    ClassroomEventType.TEACHER_FEEDBACK
                } else {
                    ClassroomEventType.GRADE_UPDATE
                }
            }
            EventClassificationLabel.UNKNOWN -> ClassroomEventType.UNKNOWN
        }

        val actionType = when (label) {
            EventClassificationLabel.DUE_DATE_TASK,
            EventClassificationLabel.TASK_REQUIRED,
            EventClassificationLabel.SUBMISSION_INSTRUCTION,
            EventClassificationLabel.TEST_OR_EXAM_INFO -> ClassroomEventActionType.TASK_REQUIRED
            EventClassificationLabel.ACTIONABLE_NO_DATE -> {
                if (createTasksFromActionableNoDateAnnouncements) ClassroomEventActionType.TASK_REQUIRED
                else ClassroomEventActionType.INFORMATION_ONLY
            }
            EventClassificationLabel.INFORMATION_ONLY,
            EventClassificationLabel.ANNOUNCEMENT_ONLY -> ClassroomEventActionType.INFORMATION_ONLY
            EventClassificationLabel.MATERIAL_ONLY -> {
                if (inputText.containsAny("read", "complete", "prepare", "revise", "before next class")) {
                    ClassroomEventActionType.TASK_REQUIRED
                } else {
                    ClassroomEventActionType.OPTIONAL_READING
                }
            }
            EventClassificationLabel.GRADE_OR_FEEDBACK -> {
                if (eventType == ClassroomEventType.TEACHER_FEEDBACK) ClassroomEventActionType.FEEDBACK_ONLY
                else ClassroomEventActionType.GRADE_INFO
            }
            EventClassificationLabel.UNKNOWN -> ClassroomEventActionType.UNKNOWN
        }

        val shouldCreateTask = when (label) {
            EventClassificationLabel.DUE_DATE_TASK,
            EventClassificationLabel.TASK_REQUIRED,
            EventClassificationLabel.SUBMISSION_INSTRUCTION,
            EventClassificationLabel.TEST_OR_EXAM_INFO -> true
            EventClassificationLabel.ACTIONABLE_NO_DATE -> createTasksFromActionableNoDateAnnouncements
            EventClassificationLabel.MATERIAL_ONLY -> actionType == ClassroomEventActionType.TASK_REQUIRED
            else -> false
        }

        val priority = when {
            dueDateMillis != null -> {
                val hoursUntilDue = (dueDateMillis - System.currentTimeMillis()) / (60L * 60L * 1000L)
                when {
                    hoursUntilDue <= 12 -> TaskPriority.URGENT
                    hoursUntilDue <= 48 -> TaskPriority.HIGH
                    else -> TaskPriority.MEDIUM
                }
            }
            label == EventClassificationLabel.TEST_OR_EXAM_INFO -> TaskPriority.HIGH
            shouldCreateTask -> TaskPriority.HIGH
            label == EventClassificationLabel.GRADE_OR_FEEDBACK -> TaskPriority.MEDIUM
            else -> TaskPriority.LOW
        }

        return EventClassificationResult(
            label = label,
            eventType = eventType,
            actionType = actionType,
            shouldCreateTask = shouldCreateTask,
            priority = priority,
            confidence = confidence,
            source = source,
            reason = reason
        )
    }

    private fun String.containsAny(vararg keywords: String): Boolean {
        val normalized = lowercase()
        return keywords.any { normalized.contains(it) }
    }
}
