package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.domain.repository.SettingsRepository

class SetThemeModeUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }
}
