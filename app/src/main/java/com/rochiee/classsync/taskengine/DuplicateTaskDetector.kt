package com.rochiee.classsync.taskengine

import com.rochiee.classsync.domain.model.AcademicTask
import kotlin.math.abs

object DuplicateTaskDetector {
    private const val DUE_DATE_WINDOW_MILLIS = 18L * 60L * 60L * 1000L

    fun findBestDuplicate(existingTasks: List<AcademicTask>, incomingTask: AcademicTask): AcademicTask? {
        return existingTasks
            .mapNotNull { existing ->
                val score = duplicateScore(existing, incomingTask)
                if (score > 0.0) existing to score else null
            }
            .maxByOrNull { it.second }
            ?.takeIf { it.second >= 0.72 }
            ?.first
    }

    fun merge(existingTask: AcademicTask, incomingTask: AcademicTask): AcademicTask {
        val existingPriority = sourcePriority(existingTask.source)
        val incomingPriority = sourcePriority(incomingTask.source)
        val preferred = if (incomingPriority >= existingPriority) incomingTask else existingTask
        val secondary = if (preferred === incomingTask) existingTask else incomingTask

        return preferred.copy(
            id = existingTask.id,
            title = chooseTitle(preferred, secondary),
            description = chooseDescription(preferred, secondary),
            courseName = chooseCourseName(preferred, secondary),
            isCompleted = existingTask.isCompleted || incomingTask.isCompleted,
            dueDate = chooseDueDate(preferred, secondary),
            priority = maxOf(existingTask.priority, incomingTask.priority),
            source = preferred.source,
            sourceId = preferred.sourceId ?: secondary.sourceId,
            sourceLink = preferred.sourceLink ?: secondary.sourceLink,
            createdAtMillis = minOf(existingTask.createdAtMillis, incomingTask.createdAtMillis),
            updatedAtMillis = maxOf(existingTask.updatedAtMillis, incomingTask.updatedAtMillis, System.currentTimeMillis())
        )
    }

    fun sourcePriority(source: String): Int {
        return when (source.lowercase()) {
            "classroom", "google classroom" -> 4
            "gmail" -> 3
            "notification" -> 2
            "manual", "raw text" -> 1
            else -> 1
        }
    }

    private fun duplicateScore(existingTask: AcademicTask, incomingTask: AcademicTask): Double {
        if (existingTask.sourceId != null &&
            incomingTask.sourceId != null &&
            existingTask.sourceId == incomingTask.sourceId
        ) {
            return 1.0
        }

        val titleSimilarity = TaskFingerprintGenerator.titleSimilarity(existingTask.title, incomingTask.title)
        val titlesMatch = titleSimilarity >= 0.72 ||
            TaskFingerprintGenerator.normalizedTitle(existingTask.title) ==
            TaskFingerprintGenerator.normalizedTitle(incomingTask.title)
        if (!titlesMatch) return 0.0

        val courseSimilarity = courseScore(existingTask.courseName, incomingTask.courseName)
        val dueDateSimilarity = dueDateScore(existingTask.dueDate, incomingTask.dueDate)

        return (titleSimilarity * 0.6) + (courseSimilarity * 0.2) + (dueDateSimilarity * 0.2)
    }

    private fun courseScore(first: String, second: String): Double {
        val normalizedFirst = TaskFingerprintGenerator.normalizedCourseName(first)
        val normalizedSecond = TaskFingerprintGenerator.normalizedCourseName(second)
        if (normalizedFirst.isBlank() || normalizedSecond.isBlank()) return 0.5
        if (normalizedFirst == normalizedSecond) return 1.0
        return TaskFingerprintGenerator.titleSimilarity(normalizedFirst, normalizedSecond)
    }

    private fun dueDateScore(first: Long?, second: Long?): Double {
        if (first == null || second == null) return 0.5
        val delta = abs(first - second)
        return if (delta <= DUE_DATE_WINDOW_MILLIS) 1.0 else 0.0
    }

    private fun chooseTitle(preferred: AcademicTask, secondary: AcademicTask): String {
        return if (preferred.title.length >= secondary.title.length) preferred.title else secondary.title
    }

    private fun chooseDescription(preferred: AcademicTask, secondary: AcademicTask): String {
        return if (preferred.description.length >= secondary.description.length) {
            preferred.description
        } else {
            secondary.description
        }
    }

    private fun chooseCourseName(preferred: AcademicTask, secondary: AcademicTask): String {
        return if (preferred.courseName.isNotBlank() && !preferred.courseName.equals("Unknown Course", ignoreCase = true)) {
            preferred.courseName
        } else {
            secondary.courseName
        }
    }

    private fun chooseDueDate(preferred: AcademicTask, secondary: AcademicTask): Long? {
        return preferred.dueDate ?: secondary.dueDate
    }
}
