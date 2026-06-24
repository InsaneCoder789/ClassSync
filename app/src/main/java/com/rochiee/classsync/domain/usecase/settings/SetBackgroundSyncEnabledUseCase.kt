package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetBackgroundSyncEnabledUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setBackgroundSyncEnabled(enabled)
    }
}
