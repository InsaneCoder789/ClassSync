package com.rochiee.classsync.domain.usecase.synclog

import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.repository.SyncLogRepository

class AddSyncLogUseCase(
    private val repository: SyncLogRepository
) {
    suspend operator fun invoke(log: SyncLog) {
        repository.addLog(log)
    }
}
