package com.rochiee.classsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val courseId: String,
    val name: String,
    val section: String?,
    val room: String?,
    val descriptionHeading: String?,
    val teacherName: String?,
    val courseState: String
)