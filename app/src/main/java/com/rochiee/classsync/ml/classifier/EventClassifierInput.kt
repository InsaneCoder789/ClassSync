package com.rochiee.classsync.ml.classifier

import com.rochiee.classsync.domain.model.TaskSource

data class EventClassifierInput(
    val title: String?,
    val body: String?,
    val courseName: String?,
    val source: TaskSource,
    val dueDateMillis: Long? = null
) {
    fun buildText(): String {
        return listOf(title, body, courseName)
            .mapNotNull { it?.trim()?.takeIf(String::isNotBlank) }
            .joinToString(" ")
            .trim()
    }
}
