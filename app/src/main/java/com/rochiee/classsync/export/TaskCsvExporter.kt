package com.rochiee.classsync.export

import com.rochiee.classsync.domain.model.AcademicTask

class TaskCsvExporter {
    fun export(tasks: List<AcademicTask>): String {
        val header = "id,title,description,courseName,isCompleted,dueDate,priority,source,sourceId,sourceLink,createdAtMillis,updatedAtMillis"
        val rows = tasks.map { task ->
            listOf(
                task.id.toString(),
                escape(task.title),
                escape(task.description),
                escape(task.courseName),
                task.isCompleted.toString(),
                task.dueDate?.toString().orEmpty(),
                task.priority.toString(),
                escape(task.source),
                escape(task.sourceId.orEmpty()),
                escape(task.sourceLink.orEmpty()),
                task.createdAtMillis.toString(),
                task.updatedAtMillis.toString()
            ).joinToString(",")
        }
        return buildString {
            appendLine(header)
            rows.forEach { appendLine(it) }
        }
    }

    private fun escape(value: String): String {
        val sanitized = sanitizeForSpreadsheetFormula(value)
        return "\"${sanitized.replace("\"", "\"\"")}\""
    }

    private fun sanitizeForSpreadsheetFormula(value: String): String {
        val trimmedLeading = value.trimStart()
        val isDangerous = trimmedLeading.startsWith("=") ||
            trimmedLeading.startsWith("+") ||
            trimmedLeading.startsWith("-") ||
            trimmedLeading.startsWith("@") ||
            value.startsWith("\t") ||
            value.startsWith("\r")
        return if (isDangerous) "'$value" else value
    }
}
