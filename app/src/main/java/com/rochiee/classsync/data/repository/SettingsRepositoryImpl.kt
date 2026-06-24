package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.preferences.SettingsDataStore
import com.rochiee.classsync.domain.model.SettingsPreferences
import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    override fun observeSettings(): Flow<SettingsPreferences> = settingsDataStore.settings

    override suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        settingsDataStore.setBackgroundSyncEnabled(enabled)
    }

    override suspend fun setGmailSyncEnabled(enabled: Boolean) {
        settingsDataStore.setGmailSyncEnabled(enabled)
    }

    override suspend fun setClassroomSyncEnabled(enabled: Boolean) {
        settingsDataStore.setClassroomSyncEnabled(enabled)
    }

    override suspend fun setNotificationParsingEnabled(enabled: Boolean) {
        settingsDataStore.setNotificationParsingEnabled(enabled)
    }

    override suspend fun setDefaultReminderHours(hours: Int) {
        settingsDataStore.setDefaultReminderHours(hours)
    }

    override suspend fun setLastSyncTimeMillis(timeMillis: Long) {
        settingsDataStore.setLastSyncTimeMillis(timeMillis)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        settingsDataStore.setOnboardingCompleted(completed)
    }

    override suspend fun setNotificationPermissionExplained(explained: Boolean) {
        settingsDataStore.setNotificationPermissionExplained(explained)
    }

    override suspend fun setClassroomPermissionExplained(explained: Boolean) {
        settingsDataStore.setClassroomPermissionExplained(explained)
    }

    override suspend fun setGmailPermissionExplained(explained: Boolean) {
        settingsDataStore.setGmailPermissionExplained(explained)
    }

    override suspend fun setDigestEnabled(enabled: Boolean) {
        settingsDataStore.setDigestEnabled(enabled)
    }

    override suspend fun setDigestHourOfDay(hour: Int) {
        settingsDataStore.setDigestHourOfDay(hour)
    }

    override suspend fun setDigestIncludeAnnouncements(enabled: Boolean) {
        settingsDataStore.setDigestIncludeAnnouncements(enabled)
    }

    override suspend fun setDigestIncludeMaterials(enabled: Boolean) {
        settingsDataStore.setDigestIncludeMaterials(enabled)
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        settingsDataStore.setThemeMode(themeMode)
    }

    override suspend fun setPersistedStudyPlanJson(json: String?) {
        settingsDataStore.setPersistedStudyPlanJson(json)
    }

    override suspend fun setPersistedExamChecklistJson(json: String?) {
        settingsDataStore.setPersistedExamChecklistJson(json)
    }
}
