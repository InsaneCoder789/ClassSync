package com.rochiee.classsync.data.remote.classroom

data class ClassroomSubmissionDto(
    val id: String,
    val courseId: String,
    val courseWorkId: String,
    val state: String,
    val assignedGrade: Double?,
    val draftGrade: Double?,
    val updateTimeMillis: Long
)