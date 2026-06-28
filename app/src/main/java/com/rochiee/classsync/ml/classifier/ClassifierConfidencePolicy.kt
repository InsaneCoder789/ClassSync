package com.rochiee.classsync.ml.classifier

object ClassifierConfidencePolicy {
    fun choose(
        ruleResult: EventClassificationResult,
        tfliteResult: EventClassificationResult?
    ): EventClassificationResult {
        if (tfliteResult == null) {
            return ruleResult.copy(
                source = ClassificationSource.HYBRID_FALLBACK,
                reason = "${ruleResult.reason} TFLite unavailable, so rule result was used."
            )
        }

        if (ruleResult.label in TRUSTED_RULE_LABELS) {
            return ruleResult.copy(
                source = ClassificationSource.HYBRID_FALLBACK,
                reason = "${ruleResult.reason} Trusted rule label kept over TFLite."
            )
        }

        return when {
            tfliteResult.confidence >= 0.80f && ruleResult.label in WEAK_RULE_LABELS -> {
                tfliteResult.copy(reason = "${tfliteResult.reason} Strong TFLite confidence over weak rule result.")
            }
            tfliteResult.confidence in 0.60f..0.7999f && isSameCategory(ruleResult, tfliteResult) -> {
                tfliteResult.copy(reason = "${tfliteResult.reason} TFLite agreed with the rule category in the medium-confidence band.")
            }
            tfliteResult.confidence < 0.60f -> {
                ruleResult.copy(
                    source = ClassificationSource.HYBRID_FALLBACK,
                    reason = "${ruleResult.reason} TFLite confidence ${tfliteResult.confidence} was below threshold."
                )
            }
            else -> {
                ruleResult.copy(
                    source = ClassificationSource.HYBRID_FALLBACK,
                    reason = "${ruleResult.reason} Rule result kept because TFLite did not meet hybrid policy."
                )
            }
        }
    }

    private val TRUSTED_RULE_LABELS = setOf(
        EventClassificationLabel.DUE_DATE_TASK,
        EventClassificationLabel.TEST_OR_EXAM_INFO,
        EventClassificationLabel.SUBMISSION_INSTRUCTION
    )

    private val WEAK_RULE_LABELS = setOf(
        EventClassificationLabel.UNKNOWN,
        EventClassificationLabel.INFORMATION_ONLY,
        EventClassificationLabel.ANNOUNCEMENT_ONLY,
        EventClassificationLabel.MATERIAL_ONLY
    )

    private fun isSameCategory(
        ruleResult: EventClassificationResult,
        tfliteResult: EventClassificationResult
    ): Boolean {
        return ruleResult.label == tfliteResult.label ||
            (ruleResult.shouldCreateTask == tfliteResult.shouldCreateTask &&
                ruleResult.actionType == tfliteResult.actionType) ||
            (ruleResult.eventType == tfliteResult.eventType)
    }
}
