package com.rochiee.classsync.auth

object GoogleScopes {
    const val GMAIL_READONLY = "https://www.googleapis.com/auth/gmail.readonly"
    const val CLASSROOM_COURSES_READONLY = "https://www.googleapis.com/auth/classroom.courses.readonly"
    const val CLASSROOM_COURSEWORK_ME_READONLY = "https://www.googleapis.com/auth/classroom.coursework.me.readonly"
    const val CLASSROOM_ANNOUNCEMENTS_READONLY = "https://www.googleapis.com/auth/classroom.announcements.readonly"
    const val CLASSROOM_COURSEWORK_MATERIALS_READONLY = "https://www.googleapis.com/auth/classroom.courseworkmaterials.readonly"
    
    val ALL_SCOPES = listOf(
        GMAIL_READONLY,
        CLASSROOM_COURSES_READONLY,
        CLASSROOM_COURSEWORK_ME_READONLY,
        CLASSROOM_ANNOUNCEMENTS_READONLY,
        CLASSROOM_COURSEWORK_MATERIALS_READONLY
    )
}
