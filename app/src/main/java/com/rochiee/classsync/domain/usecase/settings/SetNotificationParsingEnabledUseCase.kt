package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetNotificationParsingEnabledUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setNotificationParsingEnabled(enabled)
    }
}
