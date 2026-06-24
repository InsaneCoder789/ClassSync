package com.rochiee.classsync.domain.usecase.digest

import com.rochiee.classsync.digest.DigestAggregator
import com.rochiee.classsync.digest.DigestSummary
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first

class GenerateDigestSummaryUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val syncLogRepository: SyncLogRepository,
    private val settingsRepository: SettingsRepository,
    private val digestAggregator: DigestAggregator
) {
    suspend operator fun invoke(): DigestSummary {
        return digestAggregator.buildSummary(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot(),
            latestSyncLog = syncLogRepository.getLatestLog(),
            settings = settingsRepository.observeSettings().first()
        )
    }
}
