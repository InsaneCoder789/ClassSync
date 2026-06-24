package com.rochiee.classsync.bloc.classroom

sealed class ClassroomScreenEvent {
    object LoadData : ClassroomScreenEvent()
    data class SelectCourse(val courseId: String?) : ClassroomScreenEvent()
    object RefreshCourses : ClassroomScreenEvent()
    object ClearError : ClassroomScreenEvent()
}
