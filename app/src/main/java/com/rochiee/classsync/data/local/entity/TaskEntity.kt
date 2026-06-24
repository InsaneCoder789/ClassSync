package com.rochiee.classsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val courseName: String,
    val isCompleted: Boolean,
    val dueDate: Long?,
    val priority: Int,
    val source: String,
    val sourceId: String? = null,
    val sourceLink: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
