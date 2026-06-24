package com.rochiee.classsync.export

import android.content.Context
import android.os.Environment
import com.rochiee.classsync.domain.model.AcademicTask
import java.io.File

class TaskExportManager(
    private val context: Context,
    private val csvExporter: TaskCsvExporter,
    private val jsonExporter: TaskJsonExporter
) {
    fun exportTasksCsv(tasks: List<AcademicTask>): File {
        return writeExportFile(
            fileName = "classsync_tasks_${System.currentTimeMillis()}.csv",
            content = csvExporter.export(tasks)
        )
    }

    fun exportTasksJson(tasks: List<AcademicTask>): File {
        return writeExportFile(
            fileName = "classsync_tasks_${System.currentTimeMillis()}.json",
            content = jsonExporter.export(tasks)
        )
    }

    private fun writeExportFile(fileName: String, content: String): File {
        val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: File(context.filesDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val file = File(exportDir, fileName)
        file.writeText(content)
        return file
    }
}
