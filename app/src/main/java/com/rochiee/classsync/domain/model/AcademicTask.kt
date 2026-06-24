package com.rochiee.classsync.domain.model

data class AcademicTask(
    val id: Int = 0,
    val title: String,
    val description: String,
    val courseName: String,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val priority: Int = 0,
    val source: String = "Manual",
    val sourceId: String? = null,
    val sourceLink: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
