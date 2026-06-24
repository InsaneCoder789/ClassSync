package com.rochiee.classsync.bloc.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.usecase.study.GenerateStudyPlanUseCase
import com.rochiee.classsync.ui.state.UiStateJsonAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyPlanBlocViewModel(
    private val generateStudyPlanUseCase: GenerateStudyPlanUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StudyPlanState())
    val state: StateFlow<StudyPlanState> = _state.asStateFlow()

    init {
        loadPersistedPlan()
    }

    fun onEvent(event: StudyPlanEvent) {
        when (event) {
            StudyPlanEvent.GeneratePlan -> generatePlan()
            is StudyPlanEvent.ToggleBlockDone -> {
                _state.update { current ->
                    current.copy(
                        plan = current.plan?.copy(
                            items = current.plan.items.map { item ->
                                if (item.id == event.itemId) item.copy(isDone = !item.isDone) else item
                            }
                        )
                    )
                }
                persistCurrentPlan()
            }
            StudyPlanEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun generatePlan() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { generateStudyPlanUseCase() }
                .onSuccess { plan ->
                    _state.update { it.copy(isLoading = false, plan = plan) }
                    persistCurrentPlan()
                }
                .onFailure { error -> _state.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    private fun loadPersistedPlan() {
        viewModelScope.launch {
            val json = settingsRepository.observeSettings().first().persistedStudyPlanJson
            val plan = UiStateJsonAdapter.studyPlanFromJson(json) ?: return@launch
            _state.update { it.copy(plan = plan) }
        }
    }

    private fun persistCurrentPlan() {
        viewModelScope.launch {
            settingsRepository.setPersistedStudyPlanJson(
                UiStateJsonAdapter.studyPlanToJson(_state.value.plan)
            )
        }
    }
}
