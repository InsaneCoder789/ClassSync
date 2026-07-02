package com.rochiee.classsync.ml.classifier

import com.rochiee.classsync.domain.model.TaskSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleBasedEventClassifierTest {

    private val classifier = RuleBasedEventClassifier()

    @Test
    fun discussionOnlyAssignmentDoesNotCreateTask() {
        val result = classifier.classify(
            input = EventClassifierInput(
                title = "Assignment discussion tomorrow",
                body = "We will discuss the assignment tomorrow in class. No submission is required yet.",
                courseName = "Software Engineering",
                source = TaskSource.GMAIL
            ),
            createTasksFromActionableNoDateAnnouncements = true
        )

        assertEquals(EventClassificationLabel.INFORMATION_ONLY, result.label)
        assertFalse(result.shouldCreateTask)
    }

    @Test
    fun actionableAnnouncementStillCreatesTaskWhenVerbIsPresent() {
        val result = classifier.classify(
            input = EventClassifierInput(
                title = "Prepare before next class",
                body = "Read chapter 5 before next class and prepare two discussion questions.",
                courseName = "Cloud Computing",
                source = TaskSource.CLASSROOM
            ),
            createTasksFromActionableNoDateAnnouncements = true
        )

        assertEquals(EventClassificationLabel.ACTIONABLE_NO_DATE, result.label)
        assertTrue(result.shouldCreateTask)
    }

    @Test
    fun submissionPortalLanguageMapsToSubmissionInstruction() {
        val result = classifier.classify(
            input = EventClassifierInput(
                title = "Submission portal opened",
                body = "Turn in your presentation slides in the submission folder before class starts.",
                courseName = "Data Mining",
                source = TaskSource.CLASSROOM
            ),
            createTasksFromActionableNoDateAnnouncements = true
        )

        assertEquals(EventClassificationLabel.SUBMISSION_INSTRUCTION, result.label)
        assertTrue(result.shouldCreateTask)
    }

    @Test
    fun quizReminderStaysInAssessmentBucket() {
        val result = classifier.classify(
            input = EventClassifierInput(
                title = "Quiz preparation reminder",
                body = "Practice unit 3 numericals. Short quiz will be conducted tomorrow morning.",
                courseName = "Probability and Statistics",
                source = TaskSource.CLASSROOM
            ),
            createTasksFromActionableNoDateAnnouncements = true
        )

        assertEquals(EventClassificationLabel.TEST_OR_EXAM_INFO, result.label)
        assertTrue(result.shouldCreateTask)
    }
}
