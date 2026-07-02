package com.rochiee.classsync.ml.classifier

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rochiee.classsync.domain.model.TaskSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TfLiteEventClassifierInstrumentedTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val classifier = TfLiteEventClassifier(context)
    private val hybridClassifier = HybridEventClassifier(
        ruleBasedEventClassifier = RuleBasedEventClassifier(),
        tfLiteEventClassifier = classifier
    )

    @Test
    fun bundledModelClassifiesRepresentativeAcademicMessages() {
        val cases = listOf(
            FakeCase(
                title = "Submit OS Lab 4",
                body = "Upload the PDF before tomorrow 11:59 PM. Due tomorrow.",
                courseName = "Operating Systems",
                expectedLabel = EventClassificationLabel.DUE_DATE_TASK,
                expectedShouldCreateTask = true
            ),
            FakeCase(
                title = "Mid sem exam tomorrow",
                body = "Prepare all units for the DBMS exam tomorrow at 9 AM.",
                courseName = "Database Management Systems",
                expectedLabel = EventClassificationLabel.TEST_OR_EXAM_INFO,
                expectedShouldCreateTask = true
            ),
            FakeCase(
                title = "AI notes uploaded",
                body = "PDF notes for presentation have been uploaded for your reference.",
                courseName = "Artificial Intelligence",
                expectedLabel = EventClassificationLabel.MATERIAL_ONLY,
                expectedShouldCreateTask = false
            ),
            FakeCase(
                title = "Marks updated",
                body = "Marks for the recent machine learning evaluation have been posted.",
                courseName = "Machine Learning",
                expectedLabel = EventClassificationLabel.GRADE_OR_FEEDBACK,
                expectedShouldCreateTask = false
            ),
            FakeCase(
                title = "Class information",
                body = "Tomorrow's machine learning class will be online. Join using the Google Meet link at 9 AM.",
                courseName = "Machine Learning",
                expectedLabel = EventClassificationLabel.INFORMATION_ONLY,
                expectedShouldCreateTask = false
            ),
        )

        cases.forEach { case ->
            val result = classifier.classify(
                input = EventClassifierInput(
                    title = case.title,
                    body = case.body,
                    courseName = case.courseName,
                    source = TaskSource.GMAIL
                ),
                createTasksFromActionableNoDateAnnouncements = true
            )

            assertNotNull("Expected a TFLite result for ${case.title}", result)
            result ?: return@forEach

            assertEquals(case.expectedLabel, result.label)
            assertEquals(case.expectedShouldCreateTask, result.shouldCreateTask)
            assertEquals(ClassificationSource.TFLITE, result.source)
            assertTrue(
                "Expected useful confidence for ${case.title}, got ${result.confidence}",
                result.confidence >= 0.60f
            )
        }
    }

    @Test
    fun hybridClassifierHandlesHarderAdversarialCasesSafely() {
        val cases = listOf(
            FakeCase(
                title = "Assignment discussion tomorrow",
                body = "We will discuss the assignment tomorrow in class. No submission is required yet.",
                courseName = "Software Engineering",
                expectedLabel = EventClassificationLabel.INFORMATION_ONLY,
                expectedShouldCreateTask = false
            ),
            FakeCase(
                title = "Read before next class",
                body = "Read chapter 5 before next class and prepare two discussion questions.",
                courseName = "Cloud Computing",
                expectedLabel = EventClassificationLabel.ACTIONABLE_NO_DATE,
                expectedShouldCreateTask = true
            ),
            FakeCase(
                title = "Feedback returned",
                body = "Your draft has been reviewed. Read the instructor comments before resubmitting.",
                courseName = "Machine Learning",
                expectedLabel = EventClassificationLabel.GRADE_OR_FEEDBACK,
                expectedShouldCreateTask = false
            ),
            FakeCase(
                title = "Submission portal opened",
                body = "Turn in your presentation slides in the submission folder before class starts.",
                courseName = "Data Mining",
                expectedLabel = EventClassificationLabel.SUBMISSION_INSTRUCTION,
                expectedShouldCreateTask = true
            ),
            FakeCase(
                title = "Quiz preparation reminder",
                body = "Practice unit 3 numericals. Short quiz will be conducted tomorrow morning.",
                courseName = "Probability and Statistics",
                expectedLabel = EventClassificationLabel.TEST_OR_EXAM_INFO,
                expectedShouldCreateTask = true
            ),
            FakeCase(
                title = "Reference material",
                body = "Attached are optional reference slides for extra reading. No preparation needed.",
                courseName = "Cyber Security",
                expectedLabel = EventClassificationLabel.MATERIAL_ONLY,
                expectedShouldCreateTask = false
            ),
        )

        val mismatches = mutableListOf<String>()

        cases.forEach { case ->
            val result = hybridClassifier.classify(
                input = EventClassifierInput(
                    title = case.title,
                    body = case.body,
                    courseName = case.courseName,
                    source = TaskSource.CLASSROOM
                ),
                smartClassificationEnabled = true,
                tfliteClassificationEnabled = true,
                createTasksFromActionableNoDateAnnouncements = true
            )

            if (result.label != case.expectedLabel || result.shouldCreateTask != case.expectedShouldCreateTask) {
                mismatches += buildString {
                    append(case.title)
                    append(" -> expected ")
                    append(case.expectedLabel)
                    append(" / createTask=")
                    append(case.expectedShouldCreateTask)
                    append(", got ")
                    append(result.label)
                    append(" / createTask=")
                    append(result.shouldCreateTask)
                    append(" / source=")
                    append(result.source)
                    append(" / confidence=")
                    append(result.confidence)
                    append(" / reason=")
                    append(result.reason)
                }
            }
        }

        assertFalse(
            "Adversarial TFLite mismatches:\n${mismatches.joinToString(separator = "\n")}",
            mismatches.isNotEmpty()
        )
    }

    private data class FakeCase(
        val title: String,
        val body: String,
        val courseName: String,
        val expectedLabel: EventClassificationLabel,
        val expectedShouldCreateTask: Boolean
    )
}
