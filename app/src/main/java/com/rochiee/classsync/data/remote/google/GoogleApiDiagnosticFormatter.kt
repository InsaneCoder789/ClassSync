package com.rochiee.classsync.data.remote.google

import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.googleapis.json.GoogleJsonResponseException

object GoogleApiDiagnosticFormatter {
    fun build(
        providerLabel: String,
        action: String,
        error: Throwable
    ): String {
        val googleError = error as? GoogleJsonResponseException
        val statusCode = googleError?.statusCode
        val details = googleError?.details
        val reasons = details?.errors
            .orEmpty()
            .mapNotNull { it.reason?.takeIf(String::isNotBlank) }
            .distinct()
        val domains = details?.errors
            .orEmpty()
            .mapNotNull { it.domain?.takeIf(String::isNotBlank) }
            .distinct()
        val locations = details?.errors
            .orEmpty()
            .mapNotNull { errorItem ->
                listOfNotNull(
                    errorItem.location?.takeIf(String::isNotBlank),
                    errorItem.locationType?.takeIf(String::isNotBlank)
                ).takeIf { it.isNotEmpty() }?.joinToString(" / ")
            }
            .distinct()

        val rawMessage = details?.message
            ?: error.message
            ?: "No server message returned."
        val nestedCause = generateSequence(error.cause) { it.cause }
            .firstOrNull { it.message?.isNotBlank() == true }

        return buildString {
            append("Diagnostic details for ")
            append(providerLabel)
            append(" while trying to ")
            append(action)
            append(":\n")
            append("Exception: ")
            append(error::class.java.name)
            append('\n')
            if (statusCode != null) {
                append("HTTP status: ")
                append(statusCode)
                append('\n')
            }
            append("Raw message: ")
            append(rawMessage)
            append('\n')
            if (details?.code != null) {
                append("Google error code: ")
                append(details.code)
                append('\n')
            }
            appendJoined("Google reason", reasons)
            appendJoined("Google domain", domains)
            appendJoined("Error location", locations)
            details?.errors
                .orEmpty()
                .mapNotNull(GoogleJsonError.ErrorInfo::getMessage)
                .map(String::trim)
                .filter(String::isNotBlank)
                .distinct()
                .takeIf { it.isNotEmpty() }
                ?.let { messages ->
                    append("Google detail: ")
                    append(messages.joinToString(" | "))
                    append('\n')
                }
            nestedCause?.message?.takeIf(String::isNotBlank)?.let { causeMessage ->
                append("Nested cause: ")
                append(nestedCause::class.java.name)
                append(": ")
                append(causeMessage)
                append('\n')
            }
        }.trimEnd()
    }

    private fun StringBuilder.appendJoined(label: String, values: List<String>) {
        if (values.isEmpty()) return
        append(label)
        append(": ")
        append(values.joinToString(", "))
        append('\n')
    }
}
