package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetGmailPermissionExplainedUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(explained: Boolean) {
        settingsRepository.setGmailPermissionExplained(explained)
    }
}
