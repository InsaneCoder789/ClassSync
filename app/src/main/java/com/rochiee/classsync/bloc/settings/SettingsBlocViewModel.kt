package com.rochiee.classsync.bloc.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.usecase.digest.CancelDailyDigestUseCase
import com.rochiee.classsync.domain.usecase.digest.PreviewDailyDigestUseCase
import com.rochiee.classsync.domain.usecase.digest.ScheduleDailyDigestUseCase
import com.rochiee.classsync.domain.usecase.settings.ObserveSettingsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetBackgroundSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetClassroomSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDefaultReminderHoursUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestHourOfDayUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestIncludeAnnouncementsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDigestIncludeMaterialsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetGmailSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetGmailPermissionExplainedUseCase
import com.rochiee.classsync.domain.usecase.settings.SetClassroomPermissionExplainedUseCase
import com.rochiee.classsync.domain.usecase.settings.SetOnboardingCompletedUseCase
import com.rochiee.classsync.domain.usecase.settings.SetThemeModeUseCase
import com.rochiee.classsync.domain.usecase.worker.CancelBackgroundSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.ScheduleBackgroundSyncUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsBlocViewModel(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val setBackgroundSyncEnabledUseCase: SetBackgroundSyncEnabledUseCase,
    private val setGmailSyncEnabledUseCase: SetGmailSyncEnabledUseCase,
    private val setClassroomSyncEnabledUseCase: SetClassroomSyncEnabledUseCase,
    private val setDefaultReminderHoursUseCase: SetDefaultReminderHoursUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val setClassroomPermissionExplainedUseCase: SetClassroomPermissionExplainedUseCase,
    private val setGmailPermissionExplainedUseCase: SetGmailPermissionExplainedUseCase,
    private val setDigestEnabledUseCase: SetDigestEnabledUseCase,
    private val setDigestHourOfDayUseCase: SetDigestHourOfDayUseCase,
    private val setDigestIncludeAnnouncementsUseCase: SetDigestIncludeAnnouncementsUseCase,
    private val setDigestIncludeMaterialsUseCase: SetDigestIncludeMaterialsUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val scheduleDailyDigestUseCase: ScheduleDailyDigestUseCase,
    private val cancelDailyDigestUseCase: CancelDailyDigestUseCase,
    private val previewDailyDigestUseCase: PreviewDailyDigestUseCase,
    private val scheduleBackgroundSyncUseCase: ScheduleBackgroundSyncUseCase,
    private val cancelBackgroundSyncUseCase: CancelBackgroundSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(isLoading = true))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        onEvent(SettingsEvent.LoadSettings)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.LoadSettings -> observeSettings()
            is SettingsEvent.SetBackgroundSyncEnabled -> updateBackgroundSyncEnabled(event.enabled)
            is SettingsEvent.SetGmailSyncEnabled -> updateSyncSourceSetting { setGmailSyncEnabledUseCase(event.enabled) }
            is SettingsEvent.SetClassroomSyncEnabled -> updateSyncSourceSetting { setClassroomSyncEnabledUseCase(event.enabled) }
            is SettingsEvent.SetDefaultReminderHours -> updateSetting { setDefaultReminderHoursUseCase(event.hours) }
            is SettingsEvent.SetOnboardingCompleted -> updateSetting { setOnboardingCompletedUseCase(event.completed) }
            is SettingsEvent.SetClassroomPermissionExplained -> updateSetting { setClassroomPermissionExplainedUseCase(event.explained) }
            is SettingsEvent.SetGmailPermissionExplained -> updateSetting { setGmailPermissionExplainedUseCase(event.explained) }
            is SettingsEvent.SetDigestEnabled -> updateDigestEnabled(event.enabled)
            is SettingsEvent.SetDigestHourOfDay -> updateDigestHour(event.hour)
            is SettingsEvent.SetDigestIncludeAnnouncements -> updateSetting { setDigestIncludeAnnouncementsUseCase(event.enabled) }
            is SettingsEvent.SetDigestIncludeMaterials -> updateSetting { setDigestIncludeMaterialsUseCase(event.enabled) }
            is SettingsEvent.SetThemeMode -> updateSetting { setThemeModeUseCase(event.themeMode) }
            SettingsEvent.PreviewDigest -> previewDigest()
            SettingsEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeSettings() {
        observeSettingsUseCase()
            .onEach { settings ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        backgroundSyncEnabled = settings.backgroundSyncEnabled,
                        gmailSyncEnabled = settings.gmailSyncEnabled,
                        classroomSyncEnabled = settings.classroomSyncEnabled,
                        smartClassificationEnabled = settings.smartClassificationEnabled,
                        tfliteClassificationEnabled = settings.tfliteClassificationEnabled,
                        createTasksFromActionableNoDateAnnouncements =
                            settings.createTasksFromActionableNoDateAnnouncements,
                        defaultReminderHours = settings.defaultReminderHours,
                        lastSyncTimeMillis = settings.lastSyncTimeMillis,
                        onboardingCompleted = settings.onboardingCompleted,
                        classroomPermissionExplained = settings.classroomPermissionExplained,
                        gmailPermissionExplained = settings.gmailPermissionExplained,
                        digestEnabled = settings.digestEnabled,
                        digestHourOfDay = settings.digestHourOfDay,
                        digestIncludeAnnouncements = settings.digestIncludeAnnouncements,
                        digestIncludeMaterials = settings.digestIncludeMaterials,
                        themeMode = settings.themeMode,
                        errorMessage = null
                    )
                }
            }
            .catch { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun updateSetting(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun updateDigestEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                setDigestEnabledUseCase(enabled)
                if (enabled) {
                    scheduleDailyDigestUseCase(_state.value.digestHourOfDay)
                } else {
                    cancelDailyDigestUseCase()
                }
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun updateBackgroundSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                setBackgroundSyncEnabledUseCase(enabled)
                if (enabled) {
                    scheduleBackgroundSyncUseCase()
                } else {
                    cancelBackgroundSyncUseCase()
                }
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun updateSyncSourceSetting(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
                if (_state.value.backgroundSyncEnabled) {
                    scheduleBackgroundSyncUseCase()
                }
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun updateDigestHour(hour: Int) {
        viewModelScope.launch {
            try {
                setDigestHourOfDayUseCase(hour)
                if (_state.value.digestEnabled) {
                    scheduleDailyDigestUseCase(hour)
                }
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun previewDigest() {
        viewModelScope.launch {
            try {
                previewDailyDigestUseCase()
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }
}
