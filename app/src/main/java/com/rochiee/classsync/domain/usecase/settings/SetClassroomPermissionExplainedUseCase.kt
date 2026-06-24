package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetClassroomPermissionExplainedUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(explained: Boolean) {
        settingsRepository.setClassroomPermissionExplained(explained)
    }
}
