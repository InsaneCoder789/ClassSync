package com.rochiee.classsync.ml.classifier

enum class EventClassificationLabel {
    TASK_REQUIRED,
    DUE_DATE_TASK,
    ACTIONABLE_NO_DATE,
    INFORMATION_ONLY,
    ANNOUNCEMENT_ONLY,
    MATERIAL_ONLY,
    TEST_OR_EXAM_INFO,
    SUBMISSION_INSTRUCTION,
    GRADE_OR_FEEDBACK,
    UNKNOWN;

    companion object {
        fun fromRaw(value: String?): EventClassificationLabel {
            return entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: UNKNOWN
        }
    }
}
