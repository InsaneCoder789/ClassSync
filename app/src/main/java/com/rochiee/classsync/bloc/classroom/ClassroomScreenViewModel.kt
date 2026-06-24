package com.rochiee.classsync.bloc.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.dashboard.CourseDashboardAggregator
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.usecase.classroom.ObserveClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveAllEventsUseCase
import com.rochiee.classsync.domain.usecase.task.ObserveTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClassroomScreenViewModel(
    private val observeClassroomCoursesUseCase: ObserveClassroomCoursesUseCase,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val observeAllEventsUseCase: ObserveAllEventsUseCase,
    private val courseDashboardAggregator: CourseDashboardAggregator,
    private val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase,
    private val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClassroomScreenState())
    val state: StateFlow<ClassroomScreenState> = _state.asStateFlow()

    init {
        onEvent(ClassroomScreenEvent.LoadData)
    }

    fun onEvent(event: ClassroomScreenEvent) {
        when (event) {
            ClassroomScreenEvent.LoadData -> observeData()
            is ClassroomScreenEvent.SelectCourse -> {
                _state.update { current ->
                    buildState(
                        current = current.copy(selectedCourseId = event.courseId),
                        tasks = current.selectedCourseTasks,
                        events = current.selectedCourseEvents
                    )
                }
            }
            ClassroomScreenEvent.RefreshCourses -> refreshClassroomData()
            ClassroomScreenEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeData() {
        combine(
            observeClassroomCoursesUseCase(),
            observeTasksUseCase(),
            observeAllEventsUseCase()
        ) { courses, tasks, events ->
            Triple(courses, tasks, events)
        }
        .onEach { (courses, tasks, events) ->
            _state.update { current ->
                val selectedCourseId = current.selectedCourseId ?: courses.firstOrNull()?.courseId
                val baseState = current.copy(
                    isLoading = false,
                    courses = courses,
                    selectedCourseId = selectedCourseId,
                    courseSummaries = courses.map { course ->
                        courseDashboardAggregator.buildSummary(course, tasks, events)
                    }
                )
                buildState(baseState, tasks, events)
            }
        }
        .launchIn(viewModelScope)
    }

    private fun buildState(
        current: ClassroomScreenState,
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>
    ): ClassroomScreenState {
        val selectedCourse = current.courses.firstOrNull { it.courseId == current.selectedCourseId }
        val selectedTasks = tasks.filter { task -> selectedCourse?.name == task.courseName }
        val selectedEvents = events.filter { event ->
            event.courseId == current.selectedCourseId || event.courseName == selectedCourse?.name
        }
        return current.copy(
            selectedCourseTasks = selectedTasks,
            selectedCourseEvents = selectedEvents
        )
    }

    private fun refreshClassroomData() {
        _state.update { it.copy(isRefreshing = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // Sync both courses and coursework for a full Classroom refresh
                syncClassroomCoursesUseCase()
                syncClassroomCourseworkUseCase()
                _state.update { it.copy(isRefreshing = false) }
            } catch (error: Exception) {
                _state.update { it.copy(isRefreshing = false, errorMessage = error.message) }
            }
        }
    }
}
