package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetDigestHourOfDayUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(hour: Int) {
        settingsRepository.setDigestHourOfDay(hour)
    }
}
