package com.rochiee.classsync.bloc.classroom

sealed class ClassroomScreenEvent {
    object LoadData : ClassroomScreenEvent()
    data class SelectSemester(val semesterNumber: Int) : ClassroomScreenEvent()
    data class SelectSection(val sectionId: String) : ClassroomScreenEvent()
    object BackToSemesters : ClassroomScreenEvent()
    object BackToSections : ClassroomScreenEvent()
    object RefreshData : ClassroomScreenEvent()
    object ClearError : ClassroomScreenEvent()
}
