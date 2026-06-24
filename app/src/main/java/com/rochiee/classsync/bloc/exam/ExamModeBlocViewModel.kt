package com.rochiee.classsync.bloc.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.usecase.exam.GetExamModeUseCase
import com.rochiee.classsync.ui.state.UiStateJsonAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExamModeBlocViewModel(
    private val getExamModeUseCase: GetExamModeUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExamModeScreenState())
    val state: StateFlow<ExamModeScreenState> = _state.asStateFlow()

    init {
        loadPersistedChecklist()
    }

    fun onEvent(event: ExamModeScreenEvent) {
        when (event) {
            ExamModeScreenEvent.LoadExamMode -> load()
            is ExamModeScreenEvent.ToggleChecklistItem -> toggleChecklist(event.eventId, event.itemLabel)
            ExamModeScreenEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun load() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getExamModeUseCase() }
                .onSuccess { result -> _state.update { it.copy(isLoading = false, examMode = result) } }
                .onFailure { error -> _state.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    private fun toggleChecklist(eventId: String, itemLabel: String) {
        _state.update { current ->
            val existing = current.completedChecklist[eventId].orEmpty()
            val updated = if (existing.contains(itemLabel)) {
                existing - itemLabel
            } else {
                existing + itemLabel
            }
            current.copy(
                completedChecklist = current.completedChecklist.toMutableMap().apply {
                    put(eventId, updated)
                }
            )
        }
        persistChecklist()
    }

    private fun loadPersistedChecklist() {
        viewModelScope.launch {
            val json = settingsRepository.observeSettings().first().persistedExamChecklistJson
            val checklist = UiStateJsonAdapter.examChecklistFromJson(json)
            _state.update { it.copy(completedChecklist = checklist) }
        }
    }

    private fun persistChecklist() {
        viewModelScope.launch {
            settingsRepository.setPersistedExamChecklistJson(
                UiStateJsonAdapter.examChecklistToJson(_state.value.completedChecklist)
            )
        }
    }
}
