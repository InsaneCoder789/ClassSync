package com.rochiee.classsync.bloc.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.gmail.SyncGmailTasksUseCase
import com.rochiee.classsync.domain.sync.SyncRetryPolicy
import com.rochiee.classsync.domain.usecase.synclog.ClearSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.synclog.ObserveSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.worker.RunOneTimeFullSyncUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SyncBlocViewModel(
    private val observeSyncLogsUseCase: ObserveSyncLogsUseCase,
    private val clearSyncLogsUseCase: ClearSyncLogsUseCase,
    private val syncGmailTasksUseCase: SyncGmailTasksUseCase,
    private val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase,
    private val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase,
    private val runOneTimeFullSyncUseCase: RunOneTimeFullSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SyncState())
    val state: StateFlow<SyncState> = _state.asStateFlow()

    init {
        onEvent(SyncEvent.ObserveLogs)
    }

    fun onEvent(event: SyncEvent) {
        when (event) {
            SyncEvent.ObserveLogs -> observeLogs()
            SyncEvent.RunManualFullSync -> runManualFullSync()
            SyncEvent.RunAutoRefreshOnOpen -> runAutoRefreshOnOpen()
            SyncEvent.RunGmailSync -> runGmailSync()
            SyncEvent.RunClassroomSync -> runClassroomSync()
            SyncEvent.ClearLogs -> clearLogs()
            SyncEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeLogs() {
        observeSyncLogsUseCase()
            .onEach { logs ->
                _state.update {
                    it.copy(
                        logs = logs,
                        lastSyncMillis = logs.maxOfOrNull { log -> log.timestamp }
                    )
                }
            }
            .catch { error ->
                _state.update { it.copy(errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun runManualFullSync() {
        _state.update { it.copy(isSyncing = true, errorMessage = null) }
        viewModelScope.launch {
            val failures = mutableListOf<String>()
            var shouldRetryInBackground = false

            runCatching {
                syncClassroomCoursesUseCase()
                syncClassroomCourseworkUseCase()
            }.onFailure { error ->
                shouldRetryInBackground = shouldRetryInBackground || SyncRetryPolicy.shouldRetryInBackground(error)
                failures += error.message ?: "Classroom sync failed."
            }

            runCatching {
                syncGmailTasksUseCase()
            }.onFailure { error ->
                shouldRetryInBackground = shouldRetryInBackground || SyncRetryPolicy.shouldRetryInBackground(error)
                failures += error.message ?: "Gmail sync failed."
            }

            if (shouldRetryInBackground) {
                runOneTimeFullSyncUseCase()
            }

            _state.update {
                val combinedMessage = failures
                    .takeIf { messages -> messages.isNotEmpty() }
                    ?.joinToString("\n\n")
                it.copy(
                    isSyncing = false,
                    errorMessage = if (shouldRetryInBackground && combinedMessage != null) {
                        SyncRetryPolicy.backgroundRetryMessage("Google")
                    } else {
                        combinedMessage
                    }
                )
            }
        }
    }

    private fun runAutoRefreshOnOpen() {
        if (_state.value.isSyncing) return
        runManualFullSync()
    }

    private fun runGmailSync() {
        _state.update { it.copy(isSyncing = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                syncGmailTasksUseCase()
                _state.update { it.copy(isSyncing = false) }
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        isSyncing = false,
                        errorMessage = formatErrorMessage(error, "Gmail sync failed.")
                    )
                }
            }
        }
    }

    private fun runClassroomSync() {
        _state.update { it.copy(isSyncing = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                syncClassroomCoursesUseCase()
                syncClassroomCourseworkUseCase()
                _state.update { it.copy(isSyncing = false) }
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        isSyncing = false,
                        errorMessage = formatErrorMessage(error, "Classroom sync failed.")
                    )
                }
            }
        }
    }

    private fun formatErrorMessage(error: Exception, fallbackMessage: String): String {
        val baseMessage = error.message ?: fallbackMessage
        return if (SyncRetryPolicy.shouldRetryInBackground(error)) {
            runOneTimeFullSyncUseCase()
            val sourceLabel = when {
                fallbackMessage.contains("gmail", ignoreCase = true) -> "Gmail"
                fallbackMessage.contains("classroom", ignoreCase = true) -> "Classroom"
                else -> "Google"
            }
            SyncRetryPolicy.backgroundRetryMessage(sourceLabel)
        } else {
            baseMessage
        }
    }

    private fun clearLogs() {
        viewModelScope.launch {
            clearSyncLogsUseCase()
        }
    }
}
