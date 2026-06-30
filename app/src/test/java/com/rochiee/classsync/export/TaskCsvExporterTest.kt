package com.rochiee.classsync.export

import com.rochiee.classsync.domain.model.AcademicTask
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskCsvExporterTest {

    private val exporter = TaskCsvExporter()

    @Test
    fun neutralizesSpreadsheetFormulaPrefixes() {
        val csv = exporter.export(
            listOf(
                AcademicTask(
                    title = "=HYPERLINK(\"https://evil.example\")",
                    description = "+SUM(1,2)",
                    courseName = "@malicious",
                    source = "Manual"
                )
            )
        )

        assertTrue(csv.contains("\"'=HYPERLINK(\"\"https://evil.example\"\")\""))
        assertTrue(csv.contains("\"'+SUM(1,2)\""))
        assertTrue(csv.contains("\"'@malicious\""))
    }

    @Test
    fun leavesNormalTextUntouched() {
        val csv = exporter.export(
            listOf(
                AcademicTask(
                    title = "Regular task",
                    description = "Study chapter 4",
                    courseName = "Operating Systems",
                    source = "Manual"
                )
            )
        )

        assertTrue(csv.contains("\"Regular task\""))
        assertFalse(csv.contains("\"'Regular task\""))
    }
}
