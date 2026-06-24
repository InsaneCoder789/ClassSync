package com.rochiee.classsync.taskengine

import com.rochiee.classsync.data.remote.classroom.ClassroomCourseWorkDto
import com.rochiee.classsync.domain.model.AcademicTask

object ClassroomTaskParser {
    fun parse(courseWork: ClassroomCourseWorkDto, courseName: String): AcademicTask {
        return AcademicTask(
            title = courseWork.title,
            description = courseWork.description ?: "",
            courseName = courseName,
            isCompleted = courseWork.state == "TURNED_IN" || courseWork.state == "RETURNED",
            dueDate = courseWork.dueDateMillis,
            priority = 1, // Classroom tasks are high priority by default
            source = "Classroom",
            sourceId = "${courseWork.courseId}:${courseWork.id}",
            sourceLink = courseWork.alternateLink,
            createdAtMillis = courseWork.creationTimeMillis,
            updatedAtMillis = courseWork.updateTimeMillis
        )
    }
}
