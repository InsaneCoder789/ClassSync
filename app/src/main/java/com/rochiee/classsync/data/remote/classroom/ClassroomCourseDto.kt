package com.rochiee.classsync.data.remote.classroom

data class ClassroomCourseDto(
    val courseId: String,
    val name: String,
    val section: String?,
    val room: String?,
    val descriptionHeading: String?,
    val teacherName: String?,
    val courseState: String
)