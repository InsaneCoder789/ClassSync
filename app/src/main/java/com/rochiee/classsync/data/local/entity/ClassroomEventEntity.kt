package com.rochiee.classsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classroom_events")
data class ClassroomEventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val courseId: String?,
    val courseName: String?,
    val eventType: String,
    val actionType: String,
    val source: String,
    val sourceId: String?,
    val eventTimeMillis: Long,
    val dueDateMillis: Long?,
    val priority: String,
    val originalText: String?,
    val originalLink: String?,
    val convertedToTask: Boolean,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
