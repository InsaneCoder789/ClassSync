package com.rochiee.classsync.ml.classifier

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.eventengine.ClassroomEventClassifier

class RuleBasedEventClassifier {
    fun classify(
        input: EventClassifierInput,
        createTasksFromActionableNoDateAnnouncements: Boolean
    ): EventClassificationResult {
        val text = input.buildText()
        val eventType = ClassroomEventClassifier.classify(text)
        val label = when (eventType) {
            ClassroomEventType.DUE_DATE_UPDATE -> EventClassificationLabel.DUE_DATE_TASK
            ClassroomEventType.GRADE_UPDATE,
            ClassroomEventType.TEACHER_FEEDBACK -> EventClassificationLabel.GRADE_OR_FEEDBACK
            ClassroomEventType.REMINDER -> EventClassificationLabel.DUE_DATE_TASK
            ClassroomEventType.EXAM,
            ClassroomEventType.QUIZ -> EventClassificationLabel.TEST_OR_EXAM_INFO
            ClassroomEventType.ASSIGNMENT,
            ClassroomEventType.COURSEWORK -> {
                if (input.dueDateMillis != null) EventClassificationLabel.DUE_DATE_TASK
                else EventClassificationLabel.TASK_REQUIRED
            }
            ClassroomEventType.ANNOUNCEMENT -> {
                if (createTasksFromActionableNoDateAnnouncements && text.containsActionPhrase()) {
                    EventClassificationLabel.ACTIONABLE_NO_DATE
                } else {
                    EventClassificationLabel.ANNOUNCEMENT_ONLY
                }
            }
            ClassroomEventType.MATERIAL -> {
                if (text.containsActionPhrase()) EventClassificationLabel.ACTIONABLE_NO_DATE
                else EventClassificationLabel.MATERIAL_ONLY
            }
            ClassroomEventType.SUBMISSION_UPDATE -> EventClassificationLabel.SUBMISSION_INSTRUCTION
            ClassroomEventType.COMMENT -> EventClassificationLabel.INFORMATION_ONLY
            ClassroomEventType.UNKNOWN -> EventClassificationLabel.UNKNOWN
        }

        val confidence = when (label) {
            EventClassificationLabel.DUE_DATE_TASK,
            EventClassificationLabel.TEST_OR_EXAM_INFO,
            EventClassificationLabel.SUBMISSION_INSTRUCTION -> 0.92f
            EventClassificationLabel.TASK_REQUIRED,
            EventClassificationLabel.MATERIAL_ONLY,
            EventClassificationLabel.GRADE_OR_FEEDBACK -> 0.78f
            EventClassificationLabel.ACTIONABLE_NO_DATE,
            EventClassificationLabel.ANNOUNCEMENT_ONLY,
            EventClassificationLabel.INFORMATION_ONLY -> 0.62f
            EventClassificationLabel.UNKNOWN -> 0.35f
        }

        return ClassificationMapper.toResult(
            label = label,
            source = ClassificationSource.RULE_BASED,
            confidence = confidence,
            reason = "Rule-based classifier matched ${eventType.name} and mapped to $label.",
            inputText = text,
            dueDateMillis = input.dueDateMillis,
            createTasksFromActionableNoDateAnnouncements = createTasksFromActionableNoDateAnnouncements
        )
    }

    private fun String.containsActionPhrase(): Boolean {
        val normalized = lowercase()
        return listOf(
            "submit", "complete", "prepare", "revise", "bring it", "upload",
            "turn in", "solve", "finish", "work to complete", "practice problems"
        ).any { normalized.contains(it) }
    }
}
