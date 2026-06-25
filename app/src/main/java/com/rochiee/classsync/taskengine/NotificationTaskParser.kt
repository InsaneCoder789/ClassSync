package com.rochiee.classsync.taskengine

import com.rochiee.classsync.domain.model.AcademicTask

object NotificationTaskParser {
    private val keywords = listOf(
        "assignment", "due", "submit", "submission", "coursework",
        "quiz", "exam", "homework", "deadline", "posted", "turn in", "upload"
    )
    private val ignoredLineSnippets = listOf(
        "notification settings",
        "accountchooser",
        "continue=https",
        "google.com",
        "google llc"
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
        val sanitizedDescription = sanitizeNotificationText(text)

        return AcademicTask(
            title = title,
            description = sanitizedDescription,
            courseName = courseName,
            isCompleted = false,
            dueDate = dueDate,
            priority = 1,
            source = "Notification",
            sourceId = TaskFingerprintGenerator.fingerprint(title, courseName, dueDate).let { "$packageName:$it" }
        )
    }

    private fun sanitizeNotificationText(text: String): String {
        return text
            .lineSequence()
            .map { line ->
                line
                    .replace(Regex("<https?://[^>]+>"), "")
                    .replace(Regex("https?://\\S+"), "")
                    .replace(Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE), "")
                    .replace(Regex("\\s+"), " ")
                    .trim()
            }
            .filter { line ->
                line.isNotBlank() &&
                    ignoredLineSnippets.none { snippet -> line.lowercase().contains(snippet) } &&
                    line.count { it.isLetter() } >= 4
            }
            .take(4)
            .joinToString("\n")
            .take(220)
    }
}
