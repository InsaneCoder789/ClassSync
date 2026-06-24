package com.rochiee.classsync.data.remote.classroom

data class ClassroomAnnouncementDto(
    val id: String,
    val courseId: String,
    val text: String?,
    val alternateLink: String?,
    val attachmentTitles: List<String>,
    val attachmentLinks: List<String>,
    val creationTimeMillis: Long,
    val updateTimeMillis: Long
)
