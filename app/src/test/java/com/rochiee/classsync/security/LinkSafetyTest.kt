package com.rochiee.classsync.security

import com.rochiee.classsync.data.remote.gmail.GmailClassroomEmailParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkSafetyTest {

    @Test
    fun rejectsLookalikeTrustedHosts() {
        assertFalse(LinkSafety.isTrustedNotificationsRedirect("https://notifications.googleapis.com.evil.example/redirect"))
        assertFalse(LinkSafety.isTrustedAccountsUrl("https://accounts.google.com.evil.example/AccountChooser"))
        assertFalse(LinkSafety.isTrustedClassroomUrl("https://classroom.google.com.evil.example/c/123/a/456/details"))
    }

    @Test
    fun rejectsUnsafeTaskLinkSchemes() {
        assertFalse(LinkSafety.isSafeTaskSourceLink("javascript:alert(1)"))
        assertFalse(LinkSafety.isSafeTaskSourceLink("intent://classroom.google.com/#Intent;scheme=https;end"))
        assertFalse(LinkSafety.isSafeTaskSourceLink("http://classroom.google.com/c/123"))
        assertTrue(LinkSafety.isSafeTaskSourceLink("https://classroom.google.com/c/123"))
    }

    @Test
    fun gmailParserIgnoresLookalikeRedirectChains() {
        val body = """
            [Test 3rd Year](https://notifications.googleapis.com/ok)

            New assignment

            Security Test

            [See details](https://notifications.googleapis.com.evil.example/redirect)
        """.trimIndent()

        val metadata = GmailClassroomEmailParser.extractMetadata(
            subject = "New assignment: \"Security Test\"",
            body = body,
            linkResolver = {
                "https://accounts.google.com.evil.example/AccountChooser?continue=https%3A%2F%2Fclassroom.google.com%2Fc%2F123%2Fa%2F456%2Fdetails"
            }
        )

        assertNull(metadata.resolvedDetailLink)
        assertNull(metadata.stableSourceId)
    }

    @Test
    fun gmailParserAcceptsExactTrustedGoogleChain() {
        val body = """
            [Test 3rd Year](https://notifications.googleapis.com/ok)

            New assignment

            Security Test

            [See details](https://notifications.googleapis.com/email/redirect?t=abc)
        """.trimIndent()

        val metadata = GmailClassroomEmailParser.extractMetadata(
            subject = "New assignment: \"Security Test\"",
            body = body,
            linkResolver = {
                "https://accounts.google.com/AccountChooser?continue=https%3A%2F%2Fclassroom.google.com%2Fc%2F123%2Fa%2F456%2Fdetails&Email=test@example.com"
            }
        )

        assertEquals("https://classroom.google.com/c/123/a/456/details", metadata.resolvedDetailLink)
        assertEquals("classroom:/c/123/a/456/details", metadata.stableSourceId)
    }
}
