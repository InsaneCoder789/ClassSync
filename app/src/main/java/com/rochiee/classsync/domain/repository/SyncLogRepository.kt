package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.SyncLog
import kotlinx.coroutines.flow.Flow

interface SyncLogRepository {
    fun observeLogs(): Flow<List<SyncLog>>
    suspend fun addLog(log: SyncLog)
    suspend fun clearLogs()
    suspend fun getLatestLog(): SyncLog?
}
