package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.TaskSource

data class RawClassroomEventInput(
    val title: String?,
    val body: String?,
    val courseId: String?,
    val courseName: String?,
    val source: TaskSource,
    val sourceId: String?,
    val sourcePackageName: String?,
    val originalLink: String?,
    val receivedAtMillis: Long,
    val dueDateMillisOverride: Long? = null
)
