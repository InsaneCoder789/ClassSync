package com.rochiee.classsync.domain.usecase.worker

import android.content.Context
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.worker.WorkScheduler
import kotlinx.coroutines.flow.first

class ScheduleBackgroundSyncUseCase(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        val settings = settingsRepository.observeSettings().first()
        WorkScheduler.cancelSyncWork(context)

        if (!settings.backgroundSyncEnabled) {
            return
        }

        when {
            settings.gmailSyncEnabled && settings.classroomSyncEnabled -> {
                WorkScheduler.scheduleFullSync(context)
            }
            settings.gmailSyncEnabled -> {
                WorkScheduler.scheduleGmailSync(context)
            }
            settings.classroomSyncEnabled -> {
                WorkScheduler.scheduleClassroomSync(context)
            }
        }
    }
}
