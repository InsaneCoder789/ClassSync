package com.rochiee.classsync.domain.model

data class SyncLog(
    val id: Int = 0,
    val source: String,
    val status: String,
    val message: String,
    val timestamp: Long
)
