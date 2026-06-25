package com.rochiee.classsync.domain.model

data class ClassroomCatalog(
    val semesters: List<ClassroomSemester>
)

data class ClassroomSemester(
    val semesterNumber: Int,
    val sections: List<ClassroomSection>
)

data class ClassroomSection(
    val sectionId: String,
    val studentCount: Int,
    val days: List<ClassroomDaySchedule>
)

data class ClassroomDaySchedule(
    val dayKey: String,
    val label: String,
    val entries: List<ClassroomScheduleEntry>
)

data class ClassroomScheduleEntry(
    val slotIndex: Int,
    val time: String,
    val subject: String,
    val room: String,
    val variant: String?
)
