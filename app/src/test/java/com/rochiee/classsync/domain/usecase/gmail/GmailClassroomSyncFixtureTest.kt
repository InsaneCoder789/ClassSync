package com.rochiee.classsync.domain.usecase.gmail

import com.rochiee.classsync.data.remote.gmail.GmailClassroomEmailParser
import com.rochiee.classsync.data.remote.gmail.GmailMessageDto
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.SettingsPreferences
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.eventengine.ClassroomEventParser
import com.rochiee.classsync.eventengine.EventToTaskConverter
import com.rochiee.classsync.eventengine.RawClassroomEventInput
import com.rochiee.classsync.ml.classifier.HybridEventClassifier
import com.rochiee.classsync.ml.classifier.RuleBasedEventClassifier
import com.rochiee.classsync.taskengine.DuplicateTaskDetector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GmailClassroomSyncFixtureTest {

    private val parser = ClassroomEventParser(
        settingsRepository = FakeSettingsRepository(),
        hybridEventClassifier = HybridEventClassifier(
            ruleBasedEventClassifier = RuleBasedEventClassifier(),
            tfLiteEventClassifier = null
        )
    )
    private val converter = EventToTaskConverter()

    @Test
    fun classroomFixtureEmailsProduceExpectedTasksWithoutDuplicates() = runBlocking {
        val resolverMap = mapOf(
            FIXTURE_ASSIGNMENT_2.detailUrl to "https://accounts.google.com/AccountChooser?continue=https://classroom.google.com/c/ODY4NTgxMjg3MjM2/a/ODY4NTgwOTUyMTI0/details?email%3Dchatterjeerohan0204@gmail.com&Email=chatterjeerohan0204@gmail.com",
            FIXTURE_ASSIGNMENT_3.detailUrl to "https://accounts.google.com/AccountChooser?continue=https://classroom.google.com/c/ODY4NTgxMjg3MjM2/a/ODY4NjA4MzU1MDAy/details?email%3Dchatterjeerohan0204@gmail.com&Email=chatterjeerohan0204@gmail.com",
            FIXTURE_ANNOUNCEMENT.detailUrl to "https://accounts.google.com/AccountChooser?continue=https://classroom.google.com/c/ODY4NTgxMjg3MjM2/p/ODY4NTgxODI3MjE1?email%3Dchatterjeerohan0204@gmail.com&Email=chatterjeerohan0204@gmail.com",
            FIXTURE_ASSIGNMENT_2_REMINDER.detailUrl to "https://accounts.google.com/AccountChooser?continue=https://classroom.google.com/c/ODY4NTgxMjg3MjM2/a/ODY4NTgwOTUyMTI0/details?email%3Dchatterjeerohan0204@gmail.com&Email=chatterjeerohan0204@gmail.com"
        )

        val tasks = FIXTURES.mapNotNull { message ->
            val metadata = GmailClassroomEmailParser.extractMetadata(
                subject = message.subject,
                body = message.body,
                linkResolver = { resolverMap[it] }
            )
            val event = parser.parse(
                RawClassroomEventInput(
                    title = metadata.itemTitle ?: message.subject,
                    body = listOfNotNull(message.snippet, message.body).joinToString("\n"),
                    courseId = null,
                    courseName = metadata.courseName,
                    source = TaskSource.GMAIL,
                    sourceId = metadata.stableSourceId ?: message.threadId.ifBlank { message.id },
                    sourcePackageName = null,
                    originalLink = metadata.resolvedDetailLink ?: metadata.detailLink ?: message.link,
                    receivedAtMillis = message.internalDateMillis
                )
            )
            event?.let(converter::convert)
        }.fold(emptyList<AcademicTask>()) { existing, task ->
            existing.merge(task)
        }

        assertEquals(3, tasks.size)
        assertEquals(
            setOf("Test Assignment 2", "Test Assignment 3", "Test Tomorrow at night"),
            tasks.map { it.title }.toSet()
        )
        assertTrue(tasks.all { it.courseName == "Test 3rd Year" })

        val assignment2Task = tasks.firstOrNull { it.title == "Test Assignment 2" }
        assertNotNull(assignment2Task)
        assertEquals(
            "classroom:/c/ODY4NTgxMjg3MjM2/a/ODY4NTgwOTUyMTI0/details",
            assignment2Task?.sourceId
        )
    }

    private fun List<AcademicTask>.merge(incoming: AcademicTask): List<AcademicTask> {
        val duplicate = DuplicateTaskDetector.findBestDuplicate(this, incoming) ?: return this + incoming
        return map { existing ->
            if (existing == duplicate) {
                DuplicateTaskDetector.merge(existing, incoming)
            } else {
                existing
            }
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val settings = SettingsPreferences(
            gmailSyncEnabled = true,
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
        override suspend fun setLastAppOpenTimeMillis(timeMillis: Long) = Unit
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

    private data class FixtureMessage(
        val subject: String,
        val snippet: String,
        val body: String,
        val id: String,
        val threadId: String,
        val detailUrl: String,
        val internalDateMillis: Long
    ) {
        fun toDto(): GmailMessageDto = GmailMessageDto(
            id = id,
            threadId = threadId,
            subject = subject,
            from = "\"Rohan Chatterjee (Classroom)\" no-reply@classroom.google.com",
            snippet = snippet,
            body = body,
            internalDateMillis = internalDateMillis,
            link = "https://mail.google.com/mail/#all/$id"
        )
    }

    private companion object {
        private const val ASSIGNMENT_2_DETAIL_URL = "https://notifications.googleapis.com/email/redirect?t=AFG8qyUVwJcKmHRTm4ckAABbK20fj8wvj5pfWctg531BTX2-ihhch3YkZXWJL2Ose-HQujihSxpA44vL0dTbncktved5TZvVy7jxsCycp97Ru0msvg3f_ARGTqD6YzaBTHVi0nkpL2r_7IkzPbIDpJLSNSBxjTD8SQgV1t6UMQRkdqJ_HYMwUeMP-OCY2TRggs-pa2NkKkUn_LtBdL-NIECIqd5909SWm8VS6ReZMfZGw7AwUhXKuHqO7gyJBosFpMHJ3KW2i1hrYbaRaZeECMULBfLcnjjBYtmhaGZhnfc4kMndwvLQ97d-4JIEc94ABCXHXZEelFcBdP8kw0sGVcnwOM2UCo7EpiPv48coxJFLu_tHe1wnRGZ0Wk2SHSL8gYvbwBv14N8y-Y7lm-tdy4X_LeIvpM_C_vawQNS_6Ic2ufqPfg&r=eJx9jDELwjAQhX-Njk1pOwmlinVwiF3q4HicR9KS5CS5ov57CSo4uT2-975nRW5poxQg8hIkFYbZOCqQvdq92d4yJ4odcpApLNTaj4MOUorM_ldCNfSX5jSah55NrWddKfii-zCen3o8lupKApNLHXmY3Kru0YIIxZkosoVQVmWzNbnLn-tDTu3fzQu3Wkgo&s=ALHZ2r7UPUm1OAHzMR_5blaI_-oq"
        private const val ASSIGNMENT_3_DETAIL_URL = "https://notifications.googleapis.com/email/redirect?t=AFG8qyVcjECRJuXKRXDkBf4Ln5RXEu-x5FEzJ159JnI3t4BgtD4_YFhxAAyh4V8vw6vR4ErBYTYt6AUsDKdP_uKalRSv0eNVhwmv4WOSVW2SLX4bCc4n9VaShUmUMcWp057hzJ3leNSnrct47LIBd8zXNk5byROIyZnV7oNqWXpEqEJHq6S8Td2VNShi5oui148YR-QQYiYsV2Sy9hYdjg1j_ZCE4jKoVuLok4RFjNXfxIraXBktAj-qQQTzSxYJ2eSwUlZe8I98guS7FCPBtu1LAOUGn-XrIMOj-F0X2OX4MFusVoA52mnWFTZSI53HC4HcEGdVE4gPMSfbMPaIvh78Kj3-08DAtxOyiMPkd5DgAQaTZMjg18na20AOP1qX6g5NMtamlUwt5xB9_Dax6bg2UoEbJ3neAQshNcJwhH3kcqoGsA&r=eJx9jDEPgjAQhX-NjhSByYQgsYzVRQfHy3lpaUrPtEei_npDcHBye_ne-54TeeS9UoDIc5RcWGYbqECeVL-yo2POlDrkKGOcqXVfBwPknJinXwnVWd-a08U-jbe18aZSsCLfN-Z93Rndv9SdBMaQO5pgDJtaowMRSp4osYNYVmVzsEu3fG6HJbV_Nx-dpEfi&s=ALHZ2r6tm600CGbIwOeBt7Vl82EI"
        private const val ANNOUNCEMENT_DETAIL_URL = "https://notifications.googleapis.com/email/redirect?t=AFG8qyWLDCa59c5En7dOCSN8prRkr_IU7Y8m0VwzdEPrr0elczzj0KBDSgkOAQJ_eCfGfdaQA6HKYjSFoWyQB8RSyLr0jEcF6H1HIYHk-4YxrtAje7kNo9M-8nRTKReUOZL7nadVIiKU4MJp5CpxjCJUeVX1aVGTz9x6VbM-IhuaSL9OLNgUW7uZKddxzdalBNbczuQZ-DB9KPBvqjyKASlk5wcxEf6U5xEu1qvfJwufY4HpawM_ptT9nu6iqk11zYMvUJKQ9pJJqrrnCvs22LNhqlnU2re_t2H5zVxMBSKaylBPLJ8kxSBr86Bnb9dxy5g3xxWvzPKiQjB2fb3iSB4ihsXuBWMIW5pFjHcmWiEDK8UncmTt4Yc-sUa4D41oI2KjNjrw_Qk2&r=eJx9jLEKwjAURb9Gx6Y2nYRSxXRwiF1cHMPjkTQkeSV5BT9fgiJObpdz77mOeS1HIQwAbYlLY4lswAYoivObXRxRwTwCJV7ShoP7OBBMKZko_kogZvXob3f71N5K7XUn1i-a1VVqPx1GjGYJO6nAGWbMHjGTM6nt2v5ka1e_9lNNw9_NC19oRNQ&s=ALHZ2r7Qv6Z3hkHkCp37tsxetix3"
        private const val ASSIGNMENT_2_REMINDER_DETAIL_URL = "https://notifications.googleapis.com/email/redirect?t=AFG8qyXEldU3NhFE52LCP9E9cPViWCKaQZ8Cf58EPj9-g88nnXguHdDA15bvLL8dlt5iK_PSkC7aYidVOBWuiNKqTiAcgqX5bBk5dzRYgQhM89k4xC4qe7qktFVexHvBeERiZEELJ5ph7d0WankXGUxEXI4IOD74bo6VH4lIRUoYxBbCBRcOUQvxnjyG1_jKWyb_miJ7-eexzqDgjTMcR0DI769yI140-tPg0JFHKBlKUlCTp4-_vUgcDsxYUUR1WTNkq84l4d8im3ZPHpH48vv6GRT3_deL4GxDmmZElTlbHg7WGnMOJQobiTrFyf8LUpAOs5nBkWtW0y9BuQWz7EW4ZHVqVpooZJmZy85_rZNw1gbUHaY5ZVPveQtLypNwmEtiC_zlSNVkyIXD6duoEwVzRggoL59AvRTYEaFrCbRWrhNHkA&r=eJx9jbEOgkAQRL9GO1giVCYEjVhYIA0Wlst5uYPc3ZrbJcrfG6ImVnaTN_MyVuTOWwBUiqYgnBoi43SqyMP-zQ6WiHWsFAUZwqRL-3GUQ-ZI5H8lBW19Lc6deTajyZux2QB-0aPtLnPTnTLgqfcD80CBoZ8TFpSJAcMtYYqSOGRJAnoN6FylPQ5uldfKooiOo9aRLIZskxU7s3TL8fq4pPLv5gV191ZK&s=ALHZ2r5oY2cwe4kCM2Wvn8CVLvGj"

        private val FIXTURE_ASSIGNMENT_2 = FixtureMessage(
            subject = "New assignment: \"Test Assignment 2\"",
            snippet = "Notification settings Test 3rd Year New assignment Test Assignment 2 Loreal Ipsum Test 2 with Time Limit Due Jun 26 See details Posted on 2:01 AM, Jun 25 (GMT+04:00) by Rohan Chatterjee Google LLC 1600",
            body = """
                [Notification settings](https://notifications.googleapis.com/email/redirect?t=fixture-notification-settings)

                [Test 3rd Year](https://notifications.googleapis.com/email/redirect?t=fixture-course)

                New assignment

                Test Assignment 2

                Loreal Ipsum Test 2 with Time Limit

                Due Jun 26

                [See details]($ASSIGNMENT_2_DETAIL_URL)

                Posted on 2:01 AM, Jun 25 (GMT+04:00) by Rohan Chatterjee

                Google LLC 1600 Amphitheatre Parkway, Mountain View, CA 94043 USA
                This email was sent to you because you indicated that you'd like to receive email notifications from Google Classroom.
            """.trimIndent(),
            id = "19efba79251b5d43",
            threadId = "19efba79251b5d43",
            detailUrl = ASSIGNMENT_2_DETAIL_URL,
            internalDateMillis = 1_750_885_323_000L
        )
        private val FIXTURE_ASSIGNMENT_3 = FixtureMessage(
            subject = "New assignment: \"Test Assignment 3\"",
            snippet = "Notification settings Test 3rd Year New assignment Test Assignment 3 Loreal Ipsum 3 Due Jun 25 See details Posted on 11:42 AM, Jun 25 (GMT+04:00) by Rohan Chatterjee Google LLC 1600 Amphitheatre",
            body = """
                [Notification settings](https://notifications.googleapis.com/email/redirect?t=fixture-notification-settings-2)

                [Test 3rd Year](https://notifications.googleapis.com/email/redirect?t=fixture-course-2)

                New assignment

                Test Assignment 3

                Loreal Ipsum 3

                Due Jun 25

                [See details]($ASSIGNMENT_3_DETAIL_URL)

                Posted on 11:42 AM, Jun 25 (GMT+04:00) by Rohan Chatterjee

                Google LLC 1600 Amphitheatre Parkway, Mountain View, CA 94043 USA
                This email was sent to you because you indicated that you'd like to receive email notifications from Google Classroom.
            """.trimIndent(),
            id = "19efdbb8a859bb4e",
            threadId = "19efdbb8a859bb4e",
            detailUrl = ASSIGNMENT_3_DETAIL_URL,
            internalDateMillis = 1_750_931_586_000L
        )
        private val FIXTURE_ANNOUNCEMENT = FixtureMessage(
            subject = "New announcement: \"Test Tomorrow at night \"",
            snippet = "Notification settings Test 3rd Year New announcement Test Tomorrow at night See details Posted on 1:58 AM, Jun 25 (GMT+04:00) by Rohan Chatterjee Google LLC 1600 Amphitheatre Parkway, Mountain View, CA",
            body = """
                [Notification settings](https://notifications.googleapis.com/email/redirect?t=fixture-notification-settings-3)

                [Test 3rd Year](https://notifications.googleapis.com/email/redirect?t=fixture-course-3)

                New announcement

                Test Tomorrow at night

                [See details]($ANNOUNCEMENT_DETAIL_URL)

                Posted on 1:58 AM, Jun 25 (GMT+04:00) by Rohan Chatterjee

                Google LLC 1600 Amphitheatre Parkway, Mountain View, CA 94043 USA
                This email was sent to you because you indicated that you'd like to receive email notifications from Google Classroom.
            """.trimIndent(),
            id = "19efba55a0850714",
            threadId = "19efba55a0850714",
            detailUrl = ANNOUNCEMENT_DETAIL_URL,
            internalDateMillis = 1_750_877_978_000L
        )
        private val FIXTURE_ASSIGNMENT_2_REMINDER = FixtureMessage(
            subject = "Due tomorrow: \"Test Assignment 2\"",
            snippet = "Notification settings Test 3rd Year Due tomorrow Test Assignment 2 Loreal Ipsum Test 2 with Time Limit Due Jun 26 View assignment Google LLC 1600 Amphitheatre Parkway, Mountain View, CA 94043 USA This",
            body = """
                [Notification settings](https://notifications.googleapis.com/email/redirect?t=fixture-notification-settings-4)

                [Test 3rd Year](https://notifications.googleapis.com/email/redirect?t=fixture-course-4)

                Due tomorrow

                Test Assignment 2

                Loreal Ipsum Test 2 with Time Limit

                Due Jun 26

                [View assignment]($ASSIGNMENT_2_REMINDER_DETAIL_URL)

                Google LLC 1600 Amphitheatre Parkway, Mountain View, CA 94043 USA
                This email was sent to you because you indicated that you'd like to receive email notifications from Google Classroom.
            """.trimIndent(),
            id = "19f005ce4eae42eb",
            threadId = "19f005ce4eae42eb",
            detailUrl = ASSIGNMENT_2_REMINDER_DETAIL_URL,
            internalDateMillis = 1_750_956_715_000L
        )

        private val FIXTURES = listOf(
            FIXTURE_ASSIGNMENT_2.toDto(),
            FIXTURE_ASSIGNMENT_3.toDto(),
            FIXTURE_ANNOUNCEMENT.toDto(),
            FIXTURE_ASSIGNMENT_2_REMINDER.toDto()
        )
    }
}
