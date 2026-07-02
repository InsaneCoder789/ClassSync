package com.rochiee.classsync.bloc.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.usecase.event.ConvertEventToTaskUseCase
import com.rochiee.classsync.domain.usecase.event.DeleteClassroomEventUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveAllEventsUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveRecentEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventBlocViewModel(
    private val observeAllEventsUseCase: ObserveAllEventsUseCase,
    private val observeRecentEventsUseCase: ObserveRecentEventsUseCase,
    private val convertEventToTaskUseCase: ConvertEventToTaskUseCase,
    private val deleteClassroomEventUseCase: DeleteClassroomEventUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(EventState(isLoading = true))
    val state: StateFlow<EventState> = _state.asStateFlow()

    init {
        onEvent(EventEvent.LoadEvents)
        onEvent(EventEvent.LoadRecentEvents)
    }

    fun onEvent(event: EventEvent) {
        when (event) {
            EventEvent.LoadEvents -> observeAllEvents()
            is EventEvent.LoadEventsByType -> observeByType(event.type)
            EventEvent.LoadRecentEvents -> observeRecentEvents()
            is EventEvent.DeleteEvent -> deleteEvent(event.eventId)
            is EventEvent.ConvertEventToTask -> convertEvent(event.eventId)
            EventEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeAllEvents() {
        observeAllEventsUseCase()
            .onEach { events -> _state.update { buildCategorizedState(it, events) } }
            .catch { error -> _state.update { it.copy(isLoading = false, errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private fun observeByType(type: ClassroomEventType) {
        observeAllEventsUseCase()
            .onEach { events ->
                val filtered = events.filter { it.eventType == type }
                _state.update { buildCategorizedState(it, filtered) }
            }
            .catch { error -> _state.update { it.copy(errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private fun observeRecentEvents() {
        observeRecentEventsUseCase()
            .onEach { events ->
                _state.update {
                    it.copy(recentEvents = events, lastUpdatedMillis = System.currentTimeMillis())
                }
            }
            .catch { error -> _state.update { it.copy(errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private fun convertEvent(eventId: String) {
        viewModelScope.launch {
            try {
                convertEventToTaskUseCase(eventId)
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                deleteClassroomEventUseCase(eventId)
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun buildCategorizedState(current: EventState, events: List<ClassroomEvent>): EventState {
        return current.copy(
            isLoading = false,
            allEvents = events,
            assignments = events.filter { it.eventType == ClassroomEventType.ASSIGNMENT },
            coursework = events.filter { it.eventType == ClassroomEventType.COURSEWORK },
            quizzes = events.filter { it.eventType == ClassroomEventType.QUIZ },
            exams = events.filter { it.eventType == ClassroomEventType.EXAM },
            announcements = events.filter { it.eventType == ClassroomEventType.ANNOUNCEMENT },
            materials = events.filter { it.eventType == ClassroomEventType.MATERIAL },
            reminders = events.filter { it.eventType == ClassroomEventType.REMINDER },
            comments = events.filter { it.eventType == ClassroomEventType.COMMENT },
            feedback = events.filter { it.eventType == ClassroomEventType.TEACHER_FEEDBACK },
            gradeUpdates = events.filter { it.eventType == ClassroomEventType.GRADE_UPDATE },
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }
}
