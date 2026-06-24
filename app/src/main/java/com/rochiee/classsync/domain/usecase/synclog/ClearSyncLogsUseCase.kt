package com.rochiee.classsync.domain.usecase.synclog

import com.rochiee.classsync.domain.repository.SyncLogRepository

class ClearSyncLogsUseCase(
    private val repository: SyncLogRepository
) {
    suspend operator fun invoke() {
        repository.clearLogs()
    }
}
