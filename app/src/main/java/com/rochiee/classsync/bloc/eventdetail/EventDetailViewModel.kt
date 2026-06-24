package com.rochiee.classsync.bloc.eventdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.usecase.ai.SummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val classroomEventRepository: ClassroomEventRepository,
    private val summaryUseCase: SummaryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(EventDetailState())
    val state: StateFlow<EventDetailState> = _state.asStateFlow()

    fun onEvent(event: EventDetailEvent) {
        when (event) {
            is EventDetailEvent.LoadEvent -> loadEvent(event.eventId)
            is EventDetailEvent.SummarizeEvent -> summarize(event.eventId)
            EventDetailEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadEvent(eventId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { classroomEventRepository.getEventById(eventId) }
                .onSuccess { event ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            event = event,
                            errorMessage = if (event == null) "Event not found." else null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun summarize(eventId: String) {
        _state.update { it.copy(isSummarizing = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { summaryUseCase(eventId) }
                .onSuccess { result ->
                    _state.update { it.copy(isSummarizing = false, summaryResult = result, errorMessage = result.errorMessage) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSummarizing = false, errorMessage = error.message) }
                }
        }
    }
}
