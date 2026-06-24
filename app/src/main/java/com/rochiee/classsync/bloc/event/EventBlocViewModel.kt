package com.rochiee.classsync.bloc.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.usecase.event.ConvertEventToTaskUseCase
import com.rochiee.classsync.domain.usecase.event.DeleteClassroomEventUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveAllEventsUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveRecentEventsUseCase
import com.rochiee.classsync.domain.usecase.event.SaveClassroomEventUseCase
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
    private val saveClassroomEventUseCase: SaveClassroomEventUseCase,
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
            EventEvent.AddSampleAnnouncementEvent -> saveSampleEvent(sampleAnnouncement())
            EventEvent.AddSampleMaterialEvent -> saveSampleEvent(sampleMaterial())
            EventEvent.AddSampleQuizEvent -> saveSampleEvent(sampleQuiz())
            EventEvent.AddSampleCommentEvent -> saveSampleEvent(sampleComment())
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

    private fun saveSampleEvent(event: ClassroomEvent) {
        viewModelScope.launch {
            try {
                saveClassroomEventUseCase(event)
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
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

    private fun sampleAnnouncement(): ClassroomEvent = ClassroomEvent(
        id = "sample_announcement",
        title = "Announcement: Tomorrow's DBMS class will be online",
        description = "Please join using the Google Meet link at 9 AM.",
        courseName = "DBMS",
        eventType = ClassroomEventType.ANNOUNCEMENT,
        actionType = ClassroomEventActionType.INFORMATION_ONLY,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        priority = TaskPriority.LOW,
        originalText = "Announcement: Tomorrow's DBMS class will be online\nPlease join using the Google Meet link at 9 AM."
    )

    private fun sampleMaterial(): ClassroomEvent = ClassroomEvent(
        id = "sample_material",
        title = "New material posted: OS Deadlock Notes PDF",
        description = "Read Chapter 3 before next class.",
        courseName = "Operating Systems",
        eventType = ClassroomEventType.MATERIAL,
        actionType = ClassroomEventActionType.OPTIONAL_READING,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        dueDateMillis = System.currentTimeMillis() + 86_400_000L,
        priority = TaskPriority.MEDIUM,
        originalText = "New material posted: OS Deadlock Notes PDF\nRead Chapter 3 before next class."
    )

    private fun sampleQuiz(): ClassroomEvent = ClassroomEvent(
        id = "sample_quiz",
        title = "Quiz posted: Operating Systems Deadlock",
        description = "Due today 11:59 PM. Complete before deadline.",
        courseName = "Operating Systems",
        eventType = ClassroomEventType.QUIZ,
        actionType = ClassroomEventActionType.TASK_REQUIRED,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        dueDateMillis = System.currentTimeMillis() + 43_200_000L,
        priority = TaskPriority.HIGH,
        originalText = "Quiz posted: Operating Systems Deadlock\nDue today 11:59 PM. Complete before deadline."
    )

    private fun sampleComment(): ClassroomEvent = ClassroomEvent(
        id = "sample_comment",
        title = "Teacher commented on DBMS Assignment 2",
        description = "Please improve the ER diagram relationship labels.",
        courseName = "DBMS",
        eventType = ClassroomEventType.COMMENT,
        actionType = ClassroomEventActionType.FEEDBACK_ONLY,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        priority = TaskPriority.LOW,
        originalText = "Teacher commented on DBMS Assignment 2\nPlease improve the ER diagram relationship labels."
    )
}
