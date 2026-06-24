package com.rochiee.classsync.planner

data class PlannerFilter(
    val showTasks: Boolean = true,
    val showAssignments: Boolean = true,
    val showQuizzes: Boolean = true,
    val showExams: Boolean = true,
    val showAnnouncements: Boolean = true,
    val showMaterials: Boolean = true,
    val showCompleted: Boolean = true,
    val courseId: String? = null
)
