package com.rochiee.classsync.data.remote.classroom

data class ClassroomCourseWorkDto(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String?,
    val state: String,
    val workType: String,
    val alternateLink: String,
    val dueDateMillis: Long?,
    val creationTimeMillis: Long,
    val updateTimeMillis: Long
)