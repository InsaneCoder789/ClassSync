package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetDigestIncludeMaterialsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.setDigestIncludeMaterials(enabled)
    }
}
