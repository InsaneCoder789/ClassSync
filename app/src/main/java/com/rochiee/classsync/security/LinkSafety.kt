package com.rochiee.classsync.security

import java.net.URI
import java.util.Locale

object LinkSafety {
    private const val HTTPS_SCHEME = "https"
    private const val NOTIFICATIONS_HOST = "notifications.googleapis.com"
    private const val ACCOUNTS_HOST = "accounts.google.com"
    private const val CLASSROOM_HOST = "classroom.google.com"

    fun normalizeHttpsUrl(url: String?): String? {
        val trimmed = url?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val uri = runCatching { URI(trimmed) }.getOrNull() ?: return null
        if (!uri.userInfo.isNullOrBlank()) return null
        if (!uri.scheme.equals(HTTPS_SCHEME, ignoreCase = true)) return null
        val host = uri.host?.lowercase(Locale.ROOT)?.trim('.') ?: return null
        if (host.isBlank()) return null
        return uri.toString()
    }

    fun isSafeTaskSourceLink(url: String?): Boolean {
        return normalizeHttpsUrl(url) != null
    }

    fun isTrustedNotificationsRedirect(url: String?): Boolean {
        return hasExactHttpsHost(url, NOTIFICATIONS_HOST)
    }

    fun isTrustedAccountsUrl(url: String?): Boolean {
        return hasExactHttpsHost(url, ACCOUNTS_HOST)
    }

    fun isTrustedClassroomUrl(url: String?): Boolean {
        return hasExactHttpsHost(url, CLASSROOM_HOST)
    }

    private fun hasExactHttpsHost(url: String?, expectedHost: String): Boolean {
        val normalized = normalizeHttpsUrl(url) ?: return false
        val host = runCatching { URI(normalized).host?.lowercase(Locale.ROOT)?.trim('.') }.getOrNull()
        return host == expectedHost
    }
}
