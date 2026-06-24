package com.rochiee.classsync.bloc.task

import com.rochiee.classsync.domain.model.AcademicTask

data class TaskState(
    val tasks: List<AcademicTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)