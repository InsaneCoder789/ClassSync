package com.rochiee.classsync.taskengine

import com.rochiee.classsync.domain.model.AcademicTask

object NotificationTaskParser {
    private val keywords = listOf(
        "assignment", "due", "submit", "submission", "coursework",
        "quiz", "exam", "homework", "deadline", "posted", "turn in", "upload"
    )

    fun parse(packageName: String, title: String, text: String): AcademicTask? {
        val fullContent = "$title $text".lowercase()
        val isAcademic = keywords.any { fullContent.contains(it) }

        if (!isAcademic) return null

        val courseName = if (packageName == "com.google.android.apps.classroom") {
            title.split(":").firstOrNull()?.trim() ?: "Unknown Course"
        } else {
            "Unknown Course"
        }
        val dueDate = DeadlineParser.parse("$title $text")

        return AcademicTask(
            title = title,
            description = text,
            courseName = courseName,
            isCompleted = false,
            dueDate = dueDate,
            priority = 1,
            source = "Notification",
            sourceId = TaskFingerprintGenerator.fingerprint(title, courseName, dueDate).let { "$packageName:$it" }
        )
    }
}
