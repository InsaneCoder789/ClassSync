package com.rochiee.classsync.data.remote.gmail

data class GmailMessageDto(
    val id: String,
    val threadId: String,
    val subject: String?,
    val from: String?,
    val snippet: String?,
    val body: String?,
    val internalDateMillis: Long,
    val link: String
)