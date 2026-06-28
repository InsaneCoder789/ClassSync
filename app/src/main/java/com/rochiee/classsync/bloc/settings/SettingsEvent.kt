package com.rochiee.classsync.bloc.settings

import com.rochiee.classsync.domain.model.ThemeMode

sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class SetBackgroundSyncEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetGmailSyncEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetClassroomSyncEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetDefaultReminderHours(val hours: Int) : SettingsEvent()
    data class SetOnboardingCompleted(val completed: Boolean) : SettingsEvent()
    data class SetClassroomPermissionExplained(val explained: Boolean) : SettingsEvent()
    data class SetGmailPermissionExplained(val explained: Boolean) : SettingsEvent()
    data class SetDigestEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetDigestHourOfDay(val hour: Int) : SettingsEvent()
    data class SetDigestIncludeAnnouncements(val enabled: Boolean) : SettingsEvent()
    data class SetDigestIncludeMaterials(val enabled: Boolean) : SettingsEvent()
    data class SetThemeMode(val themeMode: ThemeMode) : SettingsEvent()
    data class SetLastAppOpenTime(val timeMillis: Long) : SettingsEvent()
    object PreviewDigest : SettingsEvent()
    object ClearError : SettingsEvent()
}
