package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetDefaultReminderHoursUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(hours: Int) {
        repository.setDefaultReminderHours(hours)
    }
}
