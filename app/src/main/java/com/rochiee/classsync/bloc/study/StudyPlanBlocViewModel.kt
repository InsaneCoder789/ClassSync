package com.rochiee.classsync.bloc.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.usecase.study.GenerateStudyPlanUseCase
import com.rochiee.classsync.study.StudyPlan
import com.rochiee.classsync.study.StudyPlanItem
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
            is StudyPlanEvent.AddManualBlock -> {
                addManualBlock(event.title, event.courseName, event.scheduledDateMillis, event.notes)
            }
            is StudyPlanEvent.DeleteBlock -> {
                _state.update { current ->
                    current.copy(
                        plan = current.plan?.copy(
                            items = current.plan.items.filterNot { it.id == event.itemId }
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
                .onSuccess { generatedPlan ->
                    val preservedManualItems = _state.value.plan?.items.orEmpty()
                        .filter { it.isManual }
                    val mergedPlan = generatedPlan.copy(
                        items = (generatedPlan.items + preservedManualItems)
                            .distinctBy { it.id }
                            .sortedBy { it.scheduledDateMillis }
                    )
                    _state.update { it.copy(isLoading = false, plan = mergedPlan) }
                    persistCurrentPlan()
                }
                .onFailure { error -> _state.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    private fun addManualBlock(
        title: String,
        courseName: String,
        scheduledDateMillis: Long,
        notes: String
    ) {
        val manualItem = StudyPlanItem(
            id = "manual_${System.currentTimeMillis()}",
            title = title,
            courseName = courseName.ifBlank { "Self study" },
            scheduledDateMillis = scheduledDateMillis,
            sourceType = "Manual",
            priorityExplanation = "Added by you for a focused study session.",
            estimatedEffortLabel = "Custom block",
            notes = notes.trim(),
            isManual = true
        )
        _state.update { current ->
            val currentPlan = current.plan ?: StudyPlan(
                generatedAtMillis = System.currentTimeMillis(),
                items = emptyList()
            )
            current.copy(
                plan = currentPlan.copy(
                    generatedAtMillis = System.currentTimeMillis(),
                    items = (currentPlan.items + manualItem).sortedBy { it.scheduledDateMillis }
                )
            )
        }
        persistCurrentPlan()
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
