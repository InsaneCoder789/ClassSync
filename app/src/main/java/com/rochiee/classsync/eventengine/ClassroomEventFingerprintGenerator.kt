package com.rochiee.classsync.eventengine

import java.security.MessageDigest

object ClassroomEventFingerprintGenerator {
    fun generate(input: RawClassroomEventInput, normalizedText: String): String {
        val raw = listOf(
            input.source.name,
            input.sourceId.orEmpty(),
            input.courseId.orEmpty(),
            input.courseName.orEmpty(),
            input.title.orEmpty(),
            normalizedText
        ).joinToString("|")

        return MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(24)
    }
}
