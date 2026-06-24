package com.rochiee.classsync.bloc.classroom

import com.rochiee.classsync.dashboard.CourseDashboardSummary
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent

data class ClassroomScreenState(
    val isLoading: Boolean = true,
    val courses: List<CourseEntity> = emptyList(),
    val courseSummaries: List<CourseDashboardSummary> = emptyList(),
    val selectedCourseId: String? = null,
    val selectedCourseTasks: List<AcademicTask> = emptyList(),
    val selectedCourseEvents: List<ClassroomEvent> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
