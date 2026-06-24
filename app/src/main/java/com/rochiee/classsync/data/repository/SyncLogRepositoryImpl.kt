package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.dao.SyncLogDao
import com.rochiee.classsync.data.local.mapper.toDomain
import com.rochiee.classsync.data.local.mapper.toEntity
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.repository.SyncLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncLogRepositoryImpl(
    private val syncLogDao: SyncLogDao
) : SyncLogRepository {
    override fun observeLogs(): Flow<List<SyncLog>> {
        return syncLogDao.observeLogs().map { logs -> logs.map { it.toDomain() } }
    }

    override suspend fun addLog(log: SyncLog) {
        syncLogDao.insertLog(log.toEntity())
    }

    override suspend fun clearLogs() {
        syncLogDao.clearLogs()
    }

    override suspend fun getLatestLog(): SyncLog? {
        return syncLogDao.getLatestLog()?.toDomain()
    }
}
