package com.rochiee.classsync.bloc.study

import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.study.StudyPlan

data class StudyPlanState(
    val isLoading: Boolean = false,
    val plan: StudyPlan? = null,
    val availableCourses: List<CourseEntity> = emptyList(),
    val selectedCourseIds: Set<String> = emptySet(),
    val errorMessage: String? = null
)
