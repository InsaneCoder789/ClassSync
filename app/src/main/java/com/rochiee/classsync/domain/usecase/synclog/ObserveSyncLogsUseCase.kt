package com.rochiee.classsync.domain.usecase.synclog

import com.rochiee.classsync.domain.repository.SyncLogRepository

class ObserveSyncLogsUseCase(
    private val repository: SyncLogRepository
) {
    operator fun invoke() = repository.observeLogs()
}
