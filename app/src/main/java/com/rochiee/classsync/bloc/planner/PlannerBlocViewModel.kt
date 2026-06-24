package com.rochiee.classsync.bloc.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.usecase.planner.GetMonthPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetPlannerRangeUseCase
import com.rochiee.classsync.domain.usecase.planner.GetTodayPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetWeekPlannerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlannerBlocViewModel(
    private val getTodayPlannerUseCase: GetTodayPlannerUseCase,
    private val getWeekPlannerUseCase: GetWeekPlannerUseCase,
    private val getMonthPlannerUseCase: GetMonthPlannerUseCase,
    private val getPlannerRangeUseCase: GetPlannerRangeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PlannerState())
    val state: StateFlow<PlannerState> = _state.asStateFlow()

    fun onEvent(event: PlannerEvent) {
        when (event) {
            PlannerEvent.LoadToday -> loadToday()
            PlannerEvent.LoadCurrentWeek -> loadWeek()
            PlannerEvent.LoadCurrentMonth -> loadMonth()
            is PlannerEvent.LoadRange -> loadRange(event.startMillis, event.endMillis)
            is PlannerEvent.SetFilter -> {
                _state.update { it.copy(activeFilter = event.filter) }
            }
            PlannerEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadToday() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getTodayPlannerUseCase(_state.value.activeFilter) }
                .onSuccess { day ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            today = day,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadWeek() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getWeekPlannerUseCase(_state.value.activeFilter) }
                .onSuccess { week ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentWeek = week,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadMonth() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getMonthPlannerUseCase(_state.value.activeFilter) }
                .onSuccess { month ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentMonth = month,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadRange(startMillis: Long, endMillis: Long) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getPlannerRangeUseCase(startMillis, endMillis, _state.value.activeFilter) }
                .onSuccess { days ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedRangeDays = days,
                            selectedRangeStartMillis = startMillis,
                            selectedRangeEndMillis = endMillis,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}
