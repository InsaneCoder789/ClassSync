package com.rochiee.classsync.data.remote.classroom

data class ClassroomMaterialDto(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String?,
    val alternateLink: String?,
    val attachmentTitles: List<String>,
    val attachmentLinks: List<String>,
    val creationTimeMillis: Long,
    val updateTimeMillis: Long
)
