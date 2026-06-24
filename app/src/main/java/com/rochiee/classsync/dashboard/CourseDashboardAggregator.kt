package com.rochiee.classsync.dashboard

import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType

class CourseDashboardAggregator {
    fun buildSummary(
        course: CourseEntity,
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>
    ): CourseDashboardSummary {
        val now = System.currentTimeMillis()
        val courseTasks = tasks.filter { it.courseName == course.name }
        val courseEvents = events.filter { it.courseId == course.courseId || it.courseName == course.name }

        return CourseDashboardSummary(
            courseId = course.courseId,
            courseName = course.name,
            teacherName = course.teacherName,
            pendingTaskCount = courseTasks.count { !it.isCompleted },
            overdueCount = courseTasks.count { !it.isCompleted && (it.dueDate ?: Long.MAX_VALUE) < now },
            quizExamCount = courseEvents.count { it.eventType == ClassroomEventType.QUIZ || it.eventType == ClassroomEventType.EXAM },
            announcementCount = courseEvents.count { it.eventType == ClassroomEventType.ANNOUNCEMENT },
            materialCount = courseEvents.count { it.eventType == ClassroomEventType.MATERIAL },
            recentActivityCount = courseEvents.size,
            completedTaskCount = courseTasks.count { it.isCompleted }
        )
    }
}
