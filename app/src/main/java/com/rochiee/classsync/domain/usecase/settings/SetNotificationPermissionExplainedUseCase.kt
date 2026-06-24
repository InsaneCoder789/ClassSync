package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetNotificationPermissionExplainedUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(explained: Boolean) {
        settingsRepository.setNotificationPermissionExplained(explained)
    }
}
