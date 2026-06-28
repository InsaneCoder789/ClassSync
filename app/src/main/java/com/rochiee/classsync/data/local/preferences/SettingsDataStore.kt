package com.rochiee.classsync.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rochiee.classsync.domain.model.SettingsPreferences
import com.rochiee.classsync.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "classsync_settings")

class SettingsDataStore(private val context: Context) {
    val settings: Flow<SettingsPreferences> = context.settingsDataStore.data.map { preferences ->
        SettingsPreferences(
            backgroundSyncEnabled = preferences[Keys.backgroundSyncEnabled] ?: true,
            gmailSyncEnabled = preferences[Keys.gmailSyncEnabled] ?: false,
            classroomSyncEnabled = preferences[Keys.classroomSyncEnabled] ?: true,
            smartClassificationEnabled = preferences[Keys.smartClassificationEnabled] ?: true,
            tfliteClassificationEnabled = preferences[Keys.tfliteClassificationEnabled] ?: true,
            createTasksFromActionableNoDateAnnouncements =
                preferences[Keys.createTasksFromActionableNoDateAnnouncements] ?: true,
            defaultReminderHours = preferences[Keys.defaultReminderHours] ?: 2,
            lastSyncTimeMillis = preferences[Keys.lastSyncTimeMillis],
            onboardingCompleted = preferences[Keys.onboardingCompleted] ?: false,
            classroomPermissionExplained = preferences[Keys.classroomPermissionExplained] ?: false,
            gmailPermissionExplained = preferences[Keys.gmailPermissionExplained] ?: false,
            digestEnabled = preferences[Keys.digestEnabled] ?: false,
            digestHourOfDay = preferences[Keys.digestHourOfDay] ?: 7,
            digestIncludeAnnouncements = preferences[Keys.digestIncludeAnnouncements] ?: true,
            digestIncludeMaterials = preferences[Keys.digestIncludeMaterials] ?: true,
            themeMode = preferences[Keys.themeMode]
                ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
                ?: ThemeMode.LIGHT,
            persistedStudyPlanJson = preferences[Keys.persistedStudyPlanJson],
            persistedExamChecklistJson = preferences[Keys.persistedExamChecklistJson]
        )
    }

    suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        update { it[Keys.backgroundSyncEnabled] = enabled }
    }

    suspend fun setGmailSyncEnabled(enabled: Boolean) {
        update { it[Keys.gmailSyncEnabled] = enabled }
    }

    suspend fun setClassroomSyncEnabled(enabled: Boolean) {
        update { it[Keys.classroomSyncEnabled] = enabled }
    }

    suspend fun setSmartClassificationEnabled(enabled: Boolean) {
        update { it[Keys.smartClassificationEnabled] = enabled }
    }

    suspend fun setTfliteClassificationEnabled(enabled: Boolean) {
        update { it[Keys.tfliteClassificationEnabled] = enabled }
    }

    suspend fun setCreateTasksFromActionableNoDateAnnouncements(enabled: Boolean) {
        update { it[Keys.createTasksFromActionableNoDateAnnouncements] = enabled }
    }

    suspend fun setDefaultReminderHours(hours: Int) {
        update { it[Keys.defaultReminderHours] = hours }
    }

    suspend fun setLastSyncTimeMillis(timeMillis: Long) {
        update { it[Keys.lastSyncTimeMillis] = timeMillis }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        update { it[Keys.onboardingCompleted] = completed }
    }

    suspend fun setClassroomPermissionExplained(explained: Boolean) {
        update { it[Keys.classroomPermissionExplained] = explained }
    }

    suspend fun setGmailPermissionExplained(explained: Boolean) {
        update { it[Keys.gmailPermissionExplained] = explained }
    }

    suspend fun setDigestEnabled(enabled: Boolean) {
        update { it[Keys.digestEnabled] = enabled }
    }

    suspend fun setDigestHourOfDay(hour: Int) {
        update { it[Keys.digestHourOfDay] = hour.coerceIn(0, 23) }
    }

    suspend fun setDigestIncludeAnnouncements(enabled: Boolean) {
        update { it[Keys.digestIncludeAnnouncements] = enabled }
    }

    suspend fun setDigestIncludeMaterials(enabled: Boolean) {
        update { it[Keys.digestIncludeMaterials] = enabled }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        update { it[Keys.themeMode] = themeMode.name }
    }

    suspend fun setPersistedStudyPlanJson(json: String?) {
        update { preferences ->
            if (json.isNullOrBlank()) {
                preferences.remove(Keys.persistedStudyPlanJson)
            } else {
                preferences[Keys.persistedStudyPlanJson] = json
            }
        }
    }

    suspend fun setPersistedExamChecklistJson(json: String?) {
        update { preferences ->
            if (json.isNullOrBlank()) {
                preferences.remove(Keys.persistedExamChecklistJson)
            } else {
                preferences[Keys.persistedExamChecklistJson] = json
            }
        }
    }

    private suspend fun update(block: suspend (MutablePreferencesAdapter) -> Unit) {
        context.settingsDataStore.edit { preferences ->
            block(MutablePreferencesAdapter(preferences))
        }
    }

    private class MutablePreferencesAdapter(
        private val preferences: androidx.datastore.preferences.core.MutablePreferences
    ) {
        operator fun <T> set(key: Preferences.Key<T>, value: T) {
            preferences[key] = value
        }

        fun <T> remove(key: Preferences.Key<T>) {
            preferences.remove(key)
        }
    }

    private object Keys {
        val backgroundSyncEnabled = booleanPreferencesKey("background_sync_enabled")
        val gmailSyncEnabled = booleanPreferencesKey("gmail_sync_enabled")
        val classroomSyncEnabled = booleanPreferencesKey("classroom_sync_enabled")
        val smartClassificationEnabled = booleanPreferencesKey("smart_classification_enabled")
        val tfliteClassificationEnabled = booleanPreferencesKey("tflite_classification_enabled")
        val createTasksFromActionableNoDateAnnouncements =
            booleanPreferencesKey("create_tasks_from_actionable_no_date_announcements")
        val defaultReminderHours = intPreferencesKey("default_reminder_hours")
        val lastSyncTimeMillis = longPreferencesKey("last_sync_time_millis")
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
        val classroomPermissionExplained = booleanPreferencesKey("classroom_permission_explained")
        val gmailPermissionExplained = booleanPreferencesKey("gmail_permission_explained")
        val digestEnabled = booleanPreferencesKey("digest_enabled")
        val digestHourOfDay = intPreferencesKey("digest_hour_of_day")
        val digestIncludeAnnouncements = booleanPreferencesKey("digest_include_announcements")
        val digestIncludeMaterials = booleanPreferencesKey("digest_include_materials")
        val themeMode = stringPreferencesKey("theme_mode")
        val persistedStudyPlanJson = stringPreferencesKey("persisted_study_plan_json")
        val persistedExamChecklistJson = stringPreferencesKey("persisted_exam_checklist_json")
    }
}
