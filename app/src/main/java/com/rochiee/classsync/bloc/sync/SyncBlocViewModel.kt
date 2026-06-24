package com.rochiee.classsync.bloc.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.gmail.SyncGmailTasksUseCase
import com.rochiee.classsync.domain.usecase.synclog.ClearSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.synclog.ObserveSyncLogsUseCase
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
    private val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase
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
            try {
                syncClassroomCoursesUseCase()
                syncClassroomCourseworkUseCase()
                syncGmailTasksUseCase()
                _state.update { it.copy(isSyncing = false) }
            } catch (error: Exception) {
                _state.update { it.copy(isSyncing = false, errorMessage = error.message) }
            }
        }
    }

    private fun runGmailSync() {
        _state.update { it.copy(isSyncing = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                syncGmailTasksUseCase()
                _state.update { it.copy(isSyncing = false) }
            } catch (error: Exception) {
                _state.update { it.copy(isSyncing = false, errorMessage = error.message) }
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
                _state.update { it.copy(isSyncing = false, errorMessage = error.message) }
            }
        }
    }

    private fun clearLogs() {
        viewModelScope.launch {
            clearSyncLogsUseCase()
        }
    }
}
