package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.SettingsPreferences
import com.rochiee.classsync.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<SettingsPreferences>
    suspend fun setBackgroundSyncEnabled(enabled: Boolean)
    suspend fun setGmailSyncEnabled(enabled: Boolean)
    suspend fun setClassroomSyncEnabled(enabled: Boolean)
    suspend fun setSmartClassificationEnabled(enabled: Boolean)
    suspend fun setTfliteClassificationEnabled(enabled: Boolean)
    suspend fun setCreateTasksFromActionableNoDateAnnouncements(enabled: Boolean)
    suspend fun setDefaultReminderHours(hours: Int)
    suspend fun setLastSyncTimeMillis(timeMillis: Long)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setClassroomPermissionExplained(explained: Boolean)
    suspend fun setGmailPermissionExplained(explained: Boolean)
    suspend fun setDigestEnabled(enabled: Boolean)
    suspend fun setDigestHourOfDay(hour: Int)
    suspend fun setDigestIncludeAnnouncements(enabled: Boolean)
    suspend fun setDigestIncludeMaterials(enabled: Boolean)
    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setPersistedStudyPlanJson(json: String?)
    suspend fun setPersistedExamChecklistJson(json: String?)
}
