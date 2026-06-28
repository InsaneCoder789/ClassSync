package com.rochiee.classsync.ml.classifier

class HybridEventClassifier(
    private val ruleBasedEventClassifier: RuleBasedEventClassifier,
    private val tfLiteEventClassifier: TfLiteEventClassifier?
) {
    fun classify(
        input: EventClassifierInput,
        smartClassificationEnabled: Boolean,
        tfliteClassificationEnabled: Boolean,
        createTasksFromActionableNoDateAnnouncements: Boolean
    ): EventClassificationResult {
        val ruleResult = ruleBasedEventClassifier.classify(
            input = input,
            createTasksFromActionableNoDateAnnouncements = createTasksFromActionableNoDateAnnouncements
        )

        if (!smartClassificationEnabled) {
            return ruleResult.copy(
                source = ClassificationSource.HYBRID_FALLBACK,
                reason = "${ruleResult.reason} Smart classification is disabled, so the rule result was used."
            )
        }

        if (!tfliteClassificationEnabled) {
            return ruleResult.copy(
                source = ClassificationSource.HYBRID_FALLBACK,
                reason = "${ruleResult.reason} TFLite classification is disabled, so the rule result was used."
            )
        }

        val tfliteResult = tfLiteEventClassifier?.classify(
            input = input,
            createTasksFromActionableNoDateAnnouncements = createTasksFromActionableNoDateAnnouncements
        )

        return ClassifierConfidencePolicy.choose(
            ruleResult = ruleResult,
            tfliteResult = tfliteResult
        )
    }
}
