package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority

object EventPriorityEngine {
    fun priorityFor(
        eventType: ClassroomEventType,
        actionType: ClassroomEventActionType,
        dueDateMillis: Long?,
        nowMillis: Long = System.currentTimeMillis()
    ): TaskPriority {
        if (dueDateMillis != null) {
            val hoursUntilDue = (dueDateMillis - nowMillis) / (60L * 60L * 1000L)
            if (hoursUntilDue <= 12) return TaskPriority.URGENT
            if (hoursUntilDue <= 48) return TaskPriority.HIGH
        }

        return when {
            eventType == ClassroomEventType.EXAM || eventType == ClassroomEventType.QUIZ -> TaskPriority.HIGH
            actionType == ClassroomEventActionType.TASK_REQUIRED || actionType == ClassroomEventActionType.DEADLINE_UPDATE -> TaskPriority.HIGH
            actionType == ClassroomEventActionType.OPTIONAL_READING -> TaskPriority.MEDIUM
            else -> TaskPriority.LOW
        }
    }
}
