package com.rochiee.classsync.bloc.eventdetail

import com.rochiee.classsync.ai.AiSummaryResult
import com.rochiee.classsync.domain.model.ClassroomEvent

data class EventDetailState(
    val isLoading: Boolean = false,
    val isSummarizing: Boolean = false,
    val event: ClassroomEvent? = null,
    val summaryResult: AiSummaryResult? = null,
    val errorMessage: String? = null
)
