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
        if (!settings.backgroundSyncEnabled) {
            WorkScheduler.cancelAll(context)
            return
        }

        if (settings.gmailSyncEnabled) {
            WorkScheduler.scheduleGmailSync(context)
        }
        if (settings.classroomSyncEnabled) {
            WorkScheduler.scheduleClassroomSync(context)
        }
        if (settings.gmailSyncEnabled || settings.classroomSyncEnabled) {
            WorkScheduler.scheduleFullSync(context)
        }
    }
}
