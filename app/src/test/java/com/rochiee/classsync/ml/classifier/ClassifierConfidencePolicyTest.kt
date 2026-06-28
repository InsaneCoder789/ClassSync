package com.rochiee.classsync.ml.classifier

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassifierConfidencePolicyTest {

    @Test
    fun fallsBackToRuleResultWhenTfliteIsUnavailable() {
        val ruleResult = result(
            label = EventClassificationLabel.DUE_DATE_TASK,
            confidence = 0.92f,
            source = ClassificationSource.RULE_BASED
        )

        val chosen = ClassifierConfidencePolicy.choose(ruleResult, null)

        assertEquals(EventClassificationLabel.DUE_DATE_TASK, chosen.label)
        assertEquals(ClassificationSource.HYBRID_FALLBACK, chosen.source)
        assertTrue(chosen.reason.contains("TFLite unavailable"))
    }

    @Test
    fun usesMediumConfidenceTfliteWhenCategoryAgrees() {
        val ruleResult = result(
            label = EventClassificationLabel.UNKNOWN,
            eventType = ClassroomEventType.ANNOUNCEMENT,
            actionType = ClassroomEventActionType.INFORMATION_ONLY,
            shouldCreateTask = false,
            confidence = 0.35f,
            source = ClassificationSource.RULE_BASED
        )
        val tfliteResult = result(
            label = EventClassificationLabel.ANNOUNCEMENT_ONLY,
            eventType = ClassroomEventType.ANNOUNCEMENT,
            actionType = ClassroomEventActionType.INFORMATION_ONLY,
            shouldCreateTask = false,
            confidence = 0.72f,
            source = ClassificationSource.TFLITE
        )

        val chosen = ClassifierConfidencePolicy.choose(ruleResult, tfliteResult)

        assertEquals(EventClassificationLabel.ANNOUNCEMENT_ONLY, chosen.label)
        assertEquals(ClassificationSource.TFLITE, chosen.source)
        assertTrue(chosen.reason.contains("medium-confidence band"))
    }

    private fun result(
        label: EventClassificationLabel,
        eventType: ClassroomEventType = ClassroomEventType.ASSIGNMENT,
        actionType: ClassroomEventActionType = ClassroomEventActionType.TASK_REQUIRED,
        shouldCreateTask: Boolean = true,
        confidence: Float,
        source: ClassificationSource
    ) = EventClassificationResult(
        label = label,
        eventType = eventType,
        actionType = actionType,
        shouldCreateTask = shouldCreateTask,
        priority = TaskPriority.MEDIUM,
        confidence = confidence,
        source = source,
        reason = "test"
    )
}
