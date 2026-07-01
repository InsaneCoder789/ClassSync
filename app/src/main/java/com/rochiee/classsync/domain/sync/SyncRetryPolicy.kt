package com.rochiee.classsync.domain.sync

import java.net.UnknownHostException

object SyncRetryPolicy {
    fun shouldRetryInBackground(error: Throwable): Boolean {
        return error.causeChain().any { cause ->
            cause is UnknownHostException || cause.message.matchesGoogleHostnameFailure()
        }
    }

    fun backgroundRetryMessage(sourceLabel: String? = null): String {
        val sourceText = sourceLabel?.takeIf { it.isNotBlank() }?.let { "$it sync" } ?: "Sync"
        return "$sourceText could not reach Google right now. This is usually a temporary network or DNS issue. ClassSync will keep retrying automatically in the background until Google is reachable again."
    }

    private fun Throwable.causeChain(): Sequence<Throwable> = sequence {
        var current: Throwable? = this@causeChain
        while (current != null) {
            yield(current)
            current = current.cause
        }
    }

    private fun String?.matchesGoogleHostnameFailure(): Boolean {
        val normalized = this?.lowercase()?.trim().orEmpty()
        if (normalized.isBlank()) return false

        return (
            "unknownhost" in normalized ||
                "unable to resolve host" in normalized ||
                "failed to connect to" in normalized ||
                "no address associated with hostname" in normalized ||
                "google hostname" in normalized
            ) && (
            "google" in normalized ||
                "googleapis.com" in normalized ||
                "googleusercontent.com" in normalized ||
                "gstatic.com" in normalized
            )
    }
}
