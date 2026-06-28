package com.rochiee.classsync.domain.model

data class SettingsPreferences(
    val backgroundSyncEnabled: Boolean = true,
    val gmailSyncEnabled: Boolean = false,
    val classroomSyncEnabled: Boolean = true,
    val smartClassificationEnabled: Boolean = true,
    val tfliteClassificationEnabled: Boolean = true,
    val createTasksFromActionableNoDateAnnouncements: Boolean = true,
    val defaultReminderHours: Int = 2,
    val lastSyncTimeMillis: Long? = null,
    val lastAppOpenTimeMillis: Long? = null,
    val onboardingCompleted: Boolean = false,
    val classroomPermissionExplained: Boolean = false,
    val gmailPermissionExplained: Boolean = false,
    val digestEnabled: Boolean = false,
    val digestHourOfDay: Int = 7,
    val digestIncludeAnnouncements: Boolean = true,
    val digestIncludeMaterials: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val persistedStudyPlanJson: String? = null,
    val persistedExamChecklistJson: String? = null
)
