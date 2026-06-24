package com.rochiee.classsync.data.local.mapper

import com.rochiee.classsync.data.local.entity.TaskEntity
import com.rochiee.classsync.domain.model.AcademicTask

fun TaskEntity.toAcademicTask(): AcademicTask {
    return AcademicTask(
        id = id,
        title = title,
        description = description,
        courseName = courseName,
        isCompleted = isCompleted,
        dueDate = dueDate,
        priority = priority,
        source = source,
        sourceId = sourceId,
        sourceLink = sourceLink,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

fun AcademicTask.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        courseName = courseName,
        isCompleted = isCompleted,
        dueDate = dueDate,
        priority = priority,
        source = source,
        sourceId = sourceId,
        sourceLink = sourceLink,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}
