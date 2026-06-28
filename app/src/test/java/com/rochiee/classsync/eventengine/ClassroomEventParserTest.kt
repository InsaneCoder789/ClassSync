package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.SettingsPreferences
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.ml.classifier.HybridEventClassifier
import com.rochiee.classsync.ml.classifier.RuleBasedEventClassifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassroomEventParserTest {

    private val settingsRepository = FakeSettingsRepository()
    private val parser = ClassroomEventParser(
        settingsRepository = settingsRepository,
        hybridEventClassifier = HybridEventClassifier(
            ruleBasedEventClassifier = RuleBasedEventClassifier(),
            tfLiteEventClassifier = null
        )
    )

    @Test
    fun parsesClassroomDueDateCourseworkIntoActionableAssignment() = runBlocking {
        val event = parser.parse(
            RawClassroomEventInput(
                title = "Submit OS Lab 3",
                body = "Upload the PDF before tomorrow 11:59 PM.",
                courseId = "c1",
                courseName = "Operating Systems",
                source = TaskSource.CLASSROOM,
                sourceId = "cw-1",
                sourcePackageName = null,
                originalLink = "https://classroom.google.com",
                receivedAtMillis = 1_700_000_000_000L,
                dueDateMillisOverride = 1_700_086_400_000L
            )
        )

        assertNotNull(event)
        assertEquals(TaskSource.CLASSROOM, event?.source)
        assertEquals(com.rochiee.classsync.domain.model.ClassroomEventActionType.TASK_REQUIRED, event?.actionType)
        assertTrue(
            event?.eventType == com.rochiee.classsync.domain.model.ClassroomEventType.ASSIGNMENT ||
                event?.eventType == com.rochiee.classsync.domain.model.ClassroomEventType.REMINDER
        )
    }

    @Test
    fun parsesGmailInformationalUpdateIntoAnnouncementOnly() = runBlocking {
        val event = parser.parse(
            RawClassroomEventInput(
                title = "Class timing update",
                body = "Tomorrow's lecture is moved to 2 PM. No action is needed from students.",
                courseId = null,
                courseName = "Physics",
                source = TaskSource.GMAIL,
                sourceId = "mail-1",
                sourcePackageName = null,
                originalLink = "https://mail.google.com",
                receivedAtMillis = 1_700_000_000_000L
            )
        )

        assertNotNull(event)
        assertEquals(TaskSource.GMAIL, event?.source)
        assertTrue(
            event?.eventType == com.rochiee.classsync.domain.model.ClassroomEventType.ANNOUNCEMENT ||
                event?.eventType == com.rochiee.classsync.domain.model.ClassroomEventType.COMMENT ||
                event?.eventType == com.rochiee.classsync.domain.model.ClassroomEventType.UNKNOWN
        )
        assertTrue(
            event?.actionType == com.rochiee.classsync.domain.model.ClassroomEventActionType.INFORMATION_ONLY ||
                event?.actionType == com.rochiee.classsync.domain.model.ClassroomEventActionType.UNKNOWN
        )
    }

    @Test
    fun parsesManualExamNoticeIntoRequiredTask() = runBlocking {
        val event = parser.parse(
            RawClassroomEventInput(
                title = "DBMS viva tomorrow",
                body = "Prepare all units and be ready for the viva in lab.",
                courseId = null,
                courseName = "DBMS",
                source = TaskSource.MANUAL,
                sourceId = "manual-1",
                sourcePackageName = null,
                originalLink = null,
                receivedAtMillis = 1_700_000_000_000L
            )
        )

        assertNotNull(event)
        assertEquals(TaskSource.MANUAL, event?.source)
        assertEquals(com.rochiee.classsync.domain.model.ClassroomEventActionType.TASK_REQUIRED, event?.actionType)
        assertTrue(
            event?.eventType == com.rochiee.classsync.domain.model.ClassroomEventType.EXAM ||
                event?.eventType == com.rochiee.classsync.domain.model.ClassroomEventType.QUIZ
        )
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val settings = SettingsPreferences(
            smartClassificationEnabled = true,
            tfliteClassificationEnabled = false,
            createTasksFromActionableNoDateAnnouncements = true,
            themeMode = ThemeMode.LIGHT
        )

        override fun observeSettings(): Flow<SettingsPreferences> = flowOf(settings)
        override suspend fun setBackgroundSyncEnabled(enabled: Boolean) = Unit
        override suspend fun setGmailSyncEnabled(enabled: Boolean) = Unit
        override suspend fun setClassroomSyncEnabled(enabled: Boolean) = Unit
        override suspend fun setSmartClassificationEnabled(enabled: Boolean) = Unit
        override suspend fun setTfliteClassificationEnabled(enabled: Boolean) = Unit
        override suspend fun setCreateTasksFromActionableNoDateAnnouncements(enabled: Boolean) = Unit
        override suspend fun setDefaultReminderHours(hours: Int) = Unit
        override suspend fun setLastSyncTimeMillis(timeMillis: Long) = Unit
        override suspend fun setOnboardingCompleted(completed: Boolean) = Unit
        override suspend fun setClassroomPermissionExplained(explained: Boolean) = Unit
        override suspend fun setGmailPermissionExplained(explained: Boolean) = Unit
        override suspend fun setDigestEnabled(enabled: Boolean) = Unit
        override suspend fun setDigestHourOfDay(hour: Int) = Unit
        override suspend fun setDigestIncludeAnnouncements(enabled: Boolean) = Unit
        override suspend fun setDigestIncludeMaterials(enabled: Boolean) = Unit
        override suspend fun setThemeMode(themeMode: ThemeMode) = Unit
        override suspend fun setPersistedStudyPlanJson(json: String?) = Unit
        override suspend fun setPersistedExamChecklistJson(json: String?) = Unit
    }
}
