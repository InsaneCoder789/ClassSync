package com.rochiee.classsync.data.remote.gmail

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLDecoder

data class GmailClassroomEmailMetadata(
    val courseName: String? = null,
    val itemTitle: String? = null,
    val detailLink: String? = null,
    val resolvedDetailLink: String? = null,
    val stableSourceId: String? = null
)

object GmailClassroomEmailParser {
    private val markdownLinkRegex = Regex("\\[([^\\]]+)]\\((https?://[^)]+)\\)")
    private val quotedSubjectRegex = Regex("\"([^\"]+)\"")
    private val ignoredLinkLabels = setOf(
        "Notification settings",
        "See details",
        "View assignment",
        "Unsubscribe or change your settings"
    )
    private val actionLabels = setOf(
        "New assignment",
        "Due tomorrow",
        "New announcement",
        "New material",
        "New question"
    )

    fun extractMetadata(
        subject: String?,
        body: String?,
        linkResolver: (String) -> String? = ::resolveRedirectLink
    ): GmailClassroomEmailMetadata {
        val links = extractLinks(body)
        val contentLines = extractContentLines(body)
        val courseName = extractCourseName(links, contentLines) ?: extractCourseNameFromSubject(subject)
        val itemTitle = extractItemTitle(subject, contentLines)
        val detailLink = extractDetailLink(links)
        val resolvedDetailLink = detailLink?.let(linkResolver)?.let(::extractClassroomContinueTarget)
            ?: detailLink?.let(::extractClassroomContinueTarget)
            ?: detailLink?.takeIf { isClassroomUrl(it) }
        val stableSourceId = resolvedDetailLink?.let(::stableSourceIdFromClassroomUrl)

        return GmailClassroomEmailMetadata(
            courseName = courseName,
            itemTitle = itemTitle,
            detailLink = detailLink,
            resolvedDetailLink = resolvedDetailLink,
            stableSourceId = stableSourceId
        )
    }

    private fun extractLinks(body: String?): List<Pair<String, String>> {
        if (body.isNullOrBlank()) return emptyList()
        return markdownLinkRegex.findAll(body)
            .map { it.groupValues[1].trim() to it.groupValues[2].trim() }
            .toList()
    }

    private fun extractContentLines(body: String?): List<String> {
        if (body.isNullOrBlank()) return emptyList()
        val normalizedBody = markdownLinkRegex.replace(body) { matchResult ->
            matchResult.groupValues[1].trim()
        }
        return normalizedBody
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .takeWhile { line ->
                !line.startsWith("Posted on ", ignoreCase = true) &&
                    !line.startsWith("Google LLC", ignoreCase = true) &&
                    !line.startsWith("This email was sent to you", ignoreCase = true)
            }
            .toList()
    }

    private fun extractCourseName(links: List<Pair<String, String>>, contentLines: List<String>): String? {
        val linkedCourseName = links
            .firstOrNull { (label, url) ->
                label !in ignoredLinkLabels && isClassroomOrRedirectUrl(url)
            }
            ?.first
            ?.takeIf { it.isNotBlank() }
        if (linkedCourseName != null) return linkedCourseName

        val notificationIndex = contentLines.indexOfFirst { it.equals("Notification settings", ignoreCase = true) }
        return contentLines
            .drop(if (notificationIndex >= 0) notificationIndex + 1 else 0)
            .firstOrNull { it !in actionLabels && it !in ignoredLinkLabels }
    }

    private fun extractCourseNameFromSubject(subject: String?): String? {
        return subject
            ?.substringBefore(':')
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.takeUnless { it in actionLabels }
    }

    private fun extractItemTitle(subject: String?, contentLines: List<String>): String? {
        val actionIndex = contentLines.indexOfFirst { it in actionLabels }
        if (actionIndex >= 0) {
            val bodyTitle = contentLines
                .drop(actionIndex + 1)
                .firstOrNull { line ->
                    line.isNotBlank() &&
                        line !in ignoredLinkLabels &&
                        !line.startsWith("Due ", ignoreCase = true)
                }
            if (!bodyTitle.isNullOrBlank()) return bodyTitle
        }

        return quotedSubjectRegex.find(subject.orEmpty())?.groupValues?.getOrNull(1)?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: subject?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun extractDetailLink(links: List<Pair<String, String>>): String? {
        return links.firstOrNull { (label, _) ->
            label.equals("See details", ignoreCase = true) ||
                label.equals("View assignment", ignoreCase = true)
        }?.second
    }

    private fun stableSourceIdFromClassroomUrl(url: String): String? {
        val uri = runCatching { URI(url) }.getOrNull() ?: return null
        val normalizedPath = uri.path?.trimEnd('/')?.takeIf { it.startsWith("/c/") } ?: return null
        return "classroom$linkSeparator$normalizedPath"
    }

    private fun extractClassroomContinueTarget(url: String): String? {
        val uri = runCatching { URI(url) }.getOrNull() ?: return null
        if (isClassroomUrl(url)) return url
        if (!uri.host.orEmpty().contains("accounts.google.com")) return null
        val continueValue = uri.rawQuery
            ?.split("&")
            ?.mapNotNull { part ->
                val pieces = part.split("=", limit = 2)
                if (pieces.size == 2 && pieces[0] == "continue") pieces[1] else null
            }
            ?.firstOrNull()
            ?.let { URLDecoder.decode(it, Charsets.UTF_8.name()) }
            ?: return null
        return continueValue.substringBefore("&Email=")
    }

    private fun resolveRedirectLink(url: String): String? {
        if (!isRedirectUrl(url)) return url.takeIf { isClassroomUrl(it) }
        return runCatching {
            val connection = URI(url).toURL().openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.requestMethod = "GET"
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.setRequestProperty("User-Agent", "ClassSync/1.0")
            connection.connect()
            connection.getHeaderField("Location")
        }.recover { error ->
            if (error is IOException) null else throw error
        }.getOrNull()
    }

    private fun isClassroomOrRedirectUrl(url: String): Boolean {
        return isClassroomUrl(url) || isRedirectUrl(url)
    }

    private fun isClassroomUrl(url: String): Boolean {
        val host = runCatching { URI(url).host.orEmpty() }.getOrDefault("")
        return host.contains("classroom.google.com")
    }

    private fun isRedirectUrl(url: String): Boolean {
        val host = runCatching { URI(url).host.orEmpty() }.getOrDefault("")
        return host.contains("notifications.googleapis.com")
    }

    private const val linkSeparator = ":"
}
