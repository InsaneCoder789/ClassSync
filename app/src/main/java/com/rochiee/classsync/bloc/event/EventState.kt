package com.rochiee.classsync.bloc.event

import com.rochiee.classsync.domain.model.ClassroomEvent

data class EventState(
    val isLoading: Boolean = false,
    val allEvents: List<ClassroomEvent> = emptyList(),
    val assignments: List<ClassroomEvent> = emptyList(),
    val coursework: List<ClassroomEvent> = emptyList(),
    val quizzes: List<ClassroomEvent> = emptyList(),
    val exams: List<ClassroomEvent> = emptyList(),
    val announcements: List<ClassroomEvent> = emptyList(),
    val materials: List<ClassroomEvent> = emptyList(),
    val reminders: List<ClassroomEvent> = emptyList(),
    val comments: List<ClassroomEvent> = emptyList(),
    val feedback: List<ClassroomEvent> = emptyList(),
    val gradeUpdates: List<ClassroomEvent> = emptyList(),
    val recentEvents: List<ClassroomEvent> = emptyList(),
    val errorMessage: String? = null,
    val lastUpdatedMillis: Long? = null
)
