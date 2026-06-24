package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType

object ClassroomEventClassifier {
    private val assignmentKeywords = listOf("assignment", "assigned", "submit", "upload", "turn in", "homework")
    private val courseworkKeywords = listOf("coursework", "classwork", "work posted")
    private val quizKeywords = listOf("quiz", "mcq", "test quiz")
    private val examKeywords = listOf("exam", "midsem", "endsem", "final", "viva", "practical")
    private val announcementKeywords = listOf("announcement", "announced", "notice", "informed", "tomorrow's class", "class cancelled", "class rescheduled", "online class")
    private val materialKeywords = listOf("material", "notes", "pdf", "slides", "chapter", "reading", "resource", "posted material")
    private val reminderKeywords = listOf("reminder", "due soon", "due today", "due tomorrow", "deadline")
    private val commentKeywords = listOf("commented", "private comment", "class comment")
    private val feedbackKeywords = listOf("feedback", "reviewed", "returned", "suggestion")
    private val dueDateUpdateKeywords = listOf("due date changed", "deadline changed", "extended", "extension")
    private val submissionUpdateKeywords = listOf("submitted", "turned in", "returned", "missing")
    private val gradeUpdateKeywords = listOf("grade posted", "marks", "scored", "graded")

    fun classify(text: String): ClassroomEventType {
        val normalized = text.lowercase()
        return when {
            containsAny(normalized, dueDateUpdateKeywords) -> ClassroomEventType.DUE_DATE_UPDATE
            containsAny(normalized, gradeUpdateKeywords) -> ClassroomEventType.GRADE_UPDATE
            containsAny(normalized, feedbackKeywords) -> ClassroomEventType.TEACHER_FEEDBACK
            containsAny(normalized, commentKeywords) -> ClassroomEventType.COMMENT
            containsAny(normalized, reminderKeywords) -> ClassroomEventType.REMINDER
            containsAny(normalized, examKeywords) -> ClassroomEventType.EXAM
            containsAny(normalized, quizKeywords) -> ClassroomEventType.QUIZ
            containsAny(normalized, assignmentKeywords) -> ClassroomEventType.ASSIGNMENT
            containsAny(normalized, courseworkKeywords) -> ClassroomEventType.COURSEWORK
            containsAny(normalized, announcementKeywords) -> ClassroomEventType.ANNOUNCEMENT
            containsAny(normalized, materialKeywords) -> ClassroomEventType.MATERIAL
            containsAny(normalized, submissionUpdateKeywords) -> ClassroomEventType.SUBMISSION_UPDATE
            else -> ClassroomEventType.UNKNOWN
        }
    }

    fun actionTypeFor(eventType: ClassroomEventType): ClassroomEventActionType {
        return when (eventType) {
            ClassroomEventType.ASSIGNMENT,
            ClassroomEventType.COURSEWORK,
            ClassroomEventType.QUIZ,
            ClassroomEventType.EXAM,
            ClassroomEventType.REMINDER -> ClassroomEventActionType.TASK_REQUIRED
            ClassroomEventType.MATERIAL -> ClassroomEventActionType.OPTIONAL_READING
            ClassroomEventType.ANNOUNCEMENT,
            ClassroomEventType.SUBMISSION_UPDATE -> ClassroomEventActionType.INFORMATION_ONLY
            ClassroomEventType.COMMENT,
            ClassroomEventType.TEACHER_FEEDBACK -> ClassroomEventActionType.FEEDBACK_ONLY
            ClassroomEventType.DUE_DATE_UPDATE -> ClassroomEventActionType.DEADLINE_UPDATE
            ClassroomEventType.GRADE_UPDATE -> ClassroomEventActionType.GRADE_INFO
            ClassroomEventType.UNKNOWN -> ClassroomEventActionType.UNKNOWN
        }
    }

    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { text.contains(it) }
    }
}
