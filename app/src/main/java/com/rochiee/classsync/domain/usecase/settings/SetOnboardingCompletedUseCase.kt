package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetOnboardingCompletedUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(completed: Boolean) {
        settingsRepository.setOnboardingCompleted(completed)
    }
}
