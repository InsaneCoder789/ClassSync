package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class ObserveSettingsUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.observeSettings()
}
