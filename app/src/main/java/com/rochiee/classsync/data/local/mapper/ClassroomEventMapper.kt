package com.rochiee.classsync.data.local.mapper

import com.rochiee.classsync.data.local.entity.ClassroomEventEntity
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority
import com.rochiee.classsync.domain.model.TaskSource

fun ClassroomEventEntity.toDomain(): ClassroomEvent {
    return ClassroomEvent(
        id = id,
        title = title,
        description = description,
        courseId = courseId,
        courseName = courseName,
        eventType = ClassroomEventType.valueOf(eventType),
        actionType = ClassroomEventActionType.valueOf(actionType),
        source = TaskSource.valueOf(source),
        sourceId = sourceId,
        eventTimeMillis = eventTimeMillis,
        dueDateMillis = dueDateMillis,
        priority = TaskPriority.valueOf(priority),
        originalText = originalText,
        originalLink = originalLink,
        convertedToTask = convertedToTask,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

fun ClassroomEvent.toEntity(): ClassroomEventEntity {
    return ClassroomEventEntity(
        id = id,
        title = title,
        description = description,
        courseId = courseId,
        courseName = courseName,
        eventType = eventType.name,
        actionType = actionType.name,
        source = source.name,
        sourceId = sourceId,
        eventTimeMillis = eventTimeMillis,
        dueDateMillis = dueDateMillis,
        priority = priority.name,
        originalText = originalText,
        originalLink = originalLink,
        convertedToTask = convertedToTask,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}
