package com.rochiee.classsync.bloc.exam

import com.rochiee.classsync.exam.ExamModeState

data class ExamModeScreenState(
    val isLoading: Boolean = false,
    val examMode: ExamModeState? = null,
    val completedChecklist: Map<String, Set<String>> = emptyMap(),
    val errorMessage: String? = null
)
