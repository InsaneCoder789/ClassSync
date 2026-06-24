package com.rochiee.classsync.exam

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import kotlin.math.max

class ExamModeAggregator {
    fun build(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        nowMillis: Long = System.currentTimeMillis()
    ): ExamModeState {
        val examItems = events.filter {
            it.eventType == ClassroomEventType.EXAM || it.eventType == ClassroomEventType.QUIZ
        }.filter { (it.dueDateMillis ?: it.eventTimeMillis) >= nowMillis }
            .sortedBy { it.dueDateMillis ?: it.eventTimeMillis }
            .map { exam ->
                val courseName = exam.courseName ?: "General"
                val relatedMaterials = events.filter {
                    it.courseName == courseName && it.eventType == ClassroomEventType.MATERIAL
                }.take(3).map { it.title }
                val pendingTasks = tasks.filter {
                    !it.isCompleted && it.courseName == courseName
                }.take(4).map { it.title }
                val daysLeft = max(0, (((exam.dueDateMillis ?: exam.eventTimeMillis) - nowMillis) / DAY_MILLIS).toInt())
                ExamFocusItem(
                    eventId = exam.id,
                    title = exam.title,
                    courseName = courseName,
                    daysLeft = daysLeft,
                    relatedMaterialTitles = relatedMaterials,
                    pendingTaskTitles = pendingTasks,
                    countdownLabel = if (daysLeft == 0) "Today" else "$daysLeft day${if (daysLeft == 1) "" else "s"} left",
                    checklist = listOf(
                        "Review related materials",
                        "Finish pending course tasks",
                        "Generate a study plan"
                    )
                )
            }

        return ExamModeState(
            generatedAtMillis = nowMillis,
            upcomingExams = examItems
        )
    }

    companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
