package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetLastAppOpenTimeUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(timeMillis: Long) {
        repository.setLastAppOpenTimeMillis(timeMillis)
    }
}
