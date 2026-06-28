package com.rochiee.classsync.ml.classifier

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority

data class EventClassificationResult(
    val label: EventClassificationLabel,
    val eventType: ClassroomEventType,
    val actionType: ClassroomEventActionType,
    val shouldCreateTask: Boolean,
    val priority: TaskPriority,
    val confidence: Float,
    val source: ClassificationSource,
    val reason: String
)

enum class ClassificationSource {
    RULE_BASED,
    TFLITE,
    HYBRID_FALLBACK
}
