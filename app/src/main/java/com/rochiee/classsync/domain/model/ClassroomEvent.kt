package com.rochiee.classsync.domain.model

data class ClassroomEvent(
    val id: String,
    val title: String,
    val description: String? = null,
    val courseId: String? = null,
    val courseName: String? = null,
    val eventType: ClassroomEventType,
    val actionType: ClassroomEventActionType,
    val source: TaskSource,
    val sourceId: String? = null,
    val eventTimeMillis: Long,
    val dueDateMillis: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val originalText: String? = null,
    val originalLink: String? = null,
    val convertedToTask: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
