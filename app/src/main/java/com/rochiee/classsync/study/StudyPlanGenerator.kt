package com.rochiee.classsync.study

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import java.util.Calendar
import kotlin.math.max

class StudyPlanGenerator {
    fun generate(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        selectedCourseNames: Set<String>,
        nowMillis: Long = System.currentTimeMillis()
    ): StudyPlan {
        val candidates = mutableListOf<StudyPlanItem>()
        val normalizedSelectedCourses = selectedCourseNames
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .toSet()

        val filteredTasks = if (normalizedSelectedCourses.isEmpty()) {
            tasks
        } else {
            tasks.filter { normalizedSelectedCourses.contains(it.courseName.trim().lowercase()) }
        }
        val filteredEvents = if (normalizedSelectedCourses.isEmpty()) {
            events
        } else {
            events.filter {
                normalizedSelectedCourses.contains(it.courseName?.trim()?.lowercase().orEmpty())
            }
        }

        val assignmentTasks = filteredTasks.filter { !it.isCompleted && it.dueDate != null }
            .sortedBy { it.dueDate }
        assignmentTasks.forEach { task ->
            val scheduledDate = spreadBeforeDeadline(nowMillis, task.dueDate ?: nowMillis)
            candidates += StudyPlanItem(
                id = "task_${task.id}",
                title = task.title,
                courseName = task.courseName,
                scheduledDateMillis = scheduledDate,
                sourceType = "Task",
                priorityExplanation = if ((task.dueDate ?: Long.MAX_VALUE) < nowMillis) {
                    "Overdue work needs immediate recovery time."
                } else {
                    "Scheduled before the task deadline to reduce last-minute pressure."
                },
                estimatedEffortLabel = effortFor(nowMillis, task.dueDate, task.priority),
                notes = task.description.take(140)
            )
        }

        val examEvents = filteredEvents.filter {
            it.eventType == ClassroomEventType.QUIZ || it.eventType == ClassroomEventType.EXAM
        }.sortedBy { it.dueDateMillis ?: it.eventTimeMillis }
        examEvents.forEach { event ->
            val scheduledDate = spreadBeforeDeadline(nowMillis, event.dueDateMillis ?: event.eventTimeMillis)
            candidates += StudyPlanItem(
                id = "event_${event.id}",
                title = event.title,
                courseName = event.courseName ?: "General",
                scheduledDateMillis = scheduledDate,
                sourceType = event.eventType.name,
                priorityExplanation = "Assessment prep is prioritized ahead of quizzes and exams.",
                estimatedEffortLabel = effortFor(nowMillis, event.dueDateMillis, event.priority.score),
                notes = event.description.orEmpty().take(140)
            )
        }

        val readingEvents = filteredEvents.filter { it.eventType == ClassroomEventType.MATERIAL }
            .filter { textLooksActionable(it.title, it.description.orEmpty(), it.originalText.orEmpty()) }
            .sortedBy { it.dueDateMillis ?: it.eventTimeMillis }
        readingEvents.forEach { event ->
            candidates += StudyPlanItem(
                id = "reading_${event.id}",
                title = event.title,
                courseName = event.courseName ?: "General",
                scheduledDateMillis = spreadBeforeDeadline(nowMillis, event.dueDateMillis ?: (nowMillis + DAY_MILLIS)),
                sourceType = "Reading",
                priorityExplanation = "Material review supports upcoming work and exams.",
                estimatedEffortLabel = effortFor(nowMillis, event.dueDateMillis, event.priority.score),
                notes = event.description.orEmpty().take(140)
            )
        }

        return StudyPlan(
            generatedAtMillis = nowMillis,
            items = candidates.sortedBy { it.scheduledDateMillis }
        )
    }

    private fun spreadBeforeDeadline(nowMillis: Long, deadlineMillis: Long): Long {
        val daysUntil = max(0, ((deadlineMillis - nowMillis) / DAY_MILLIS).toInt())
        val offsetDays = when {
            daysUntil >= 7 -> 2
            daysUntil >= 3 -> 1
            else -> 0
        }
        return startOfDay(nowMillis + offsetDays * DAY_MILLIS)
    }

    private fun effortFor(nowMillis: Long, dueDateMillis: Long?, priorityScore: Int): String {
        val hoursLeft = dueDateMillis?.let { (it - nowMillis) / (60L * 60L * 1000L) } ?: Long.MAX_VALUE
        return when {
            hoursLeft <= 24 || priorityScore >= 4 -> "High effort"
            hoursLeft <= 72 || priorityScore >= 3 -> "Medium effort"
            else -> "Light effort"
        }
    }

    private fun textLooksActionable(vararg parts: String): Boolean {
        val text = parts.joinToString(" ").lowercase()
        return listOf("read", "prepare", "revise", "before next class", "study").any { text.contains(it) }
    }

    private fun startOfDay(timeMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
