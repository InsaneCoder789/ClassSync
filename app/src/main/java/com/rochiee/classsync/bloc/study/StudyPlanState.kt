package com.rochiee.classsync.bloc.study

import com.rochiee.classsync.study.StudyPlan

data class StudyPlanState(
    val isLoading: Boolean = false,
    val plan: StudyPlan? = null,
    val errorMessage: String? = null
)
