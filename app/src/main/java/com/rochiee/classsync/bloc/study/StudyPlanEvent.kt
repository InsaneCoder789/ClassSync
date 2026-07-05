package com.rochiee.classsync.bloc.study

sealed class StudyPlanEvent {
    object GeneratePlan : StudyPlanEvent()
    data class ToggleCourseSelection(val courseId: String) : StudyPlanEvent()
    object SelectAllCourses : StudyPlanEvent()
    object ClearCourseSelection : StudyPlanEvent()
    data class ToggleBlockDone(val itemId: String) : StudyPlanEvent()
    data class AddManualBlock(
        val title: String,
        val courseName: String,
        val scheduledDateMillis: Long,
        val notes: String
    ) : StudyPlanEvent()
    data class DeleteBlock(val itemId: String) : StudyPlanEvent()
    object ClearError : StudyPlanEvent()
}
