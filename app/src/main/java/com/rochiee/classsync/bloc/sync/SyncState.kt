package com.rochiee.classsync.bloc.sync

import com.rochiee.classsync.domain.model.SyncLog

data class SyncState(
    val isSyncing: Boolean = false,
    val logs: List<SyncLog> = emptyList(),
    val lastSyncMillis: Long? = null,
    val errorMessage: String? = null
)
