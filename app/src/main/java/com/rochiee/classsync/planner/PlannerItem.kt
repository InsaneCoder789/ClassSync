package com.rochiee.classsync.planner

import com.rochiee.classsync.domain.model.TaskPriority

data class PlannerItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val courseId: String? = null,
    val courseName: String? = null,
    val itemType: PlannerItemType,
    val sourceId: String? = null,
    val sourceType: String,
    val dateMillis: Long,
    val dueDateMillis: Long? = null,
    val priority: TaskPriority,
    val isCompleted: Boolean = false,
    val originalLink: String? = null
)
