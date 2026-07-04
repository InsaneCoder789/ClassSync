package com.rochiee.classsync.data.remote.gmail

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.rochiee.classsync.data.remote.google.GoogleApiDiagnosticFormatter

object GmailErrorInterpreter {
    fun toUserMessage(
        error: Throwable,
        action: String,
        accountEmail: String? = null
    ): String {
        if (error is UserRecoverableAuthIOException) {
            return buildString {
                append("Gmail access still needs your approval on this device. Sign out of ClassSync, sign in again, and accept the Gmail permission prompt before retrying ")
                append(action)
                append(".\n\n")
                append(GoogleApiDiagnosticFormatter.build("Gmail", action, error))
            }
        }

        val googleError = error as? GoogleJsonResponseException
        val statusCode = googleError?.statusCode
        val rawMessage = googleError?.details?.message
            ?: error.message
            ?: "Gmail request failed."
        val normalized = rawMessage.lowercase()
        val googleReasons = googleError?.details?.errors
            ?.mapNotNull { it.reason?.lowercase() }
            .orEmpty()
        val isPersonalAccount = accountEmail?.substringAfter('@', missingDelimiterValue = "")
            ?.equals("gmail.com", ignoreCase = true) == true
        val signedInLabel = accountEmail?.takeIf { it.isNotBlank() } ?: "this Google account"

        val summary = when {
            statusCode == 401 -> {
                "Your Gmail session expired. Sign in again, then retry $action."
            }
            statusCode == 403 && (
                "api" in normalized && "disabled" in normalized ||
                    googleReasons.any { it.contains("accessnotconfigured") || it.contains("api_disabled") }
                ) -> {
                "The Gmail API is not enabled for this Google Cloud project yet. Enable it in Google Cloud Console, then try again."
            }
            statusCode == 403 && (
                "permission" in normalized ||
                    "permission_denied" in normalized ||
                    "forbidden" in normalized ||
                    "access denied" in normalized ||
                    googleReasons.any { reason ->
                        reason.contains("forbidden") ||
                            reason.contains("permission") ||
                            reason.contains("access_denied")
                    }
                ) -> {
                if (isPersonalAccount) {
                    "$signedInLabel signed in successfully, but Gmail denied the requested read-only access for this app. Sign out, sign in again, and make sure Gmail access is approved for the same account before retrying $action."
                } else {
                    "$signedInLabel signed in successfully, but Gmail API access was denied for that account. If this is a school-managed Workspace account, the Google Workspace admin may need to allow third-party Gmail access before ClassSync can $action."
                }
            }
            statusCode == 404 -> {
                "Some Gmail data could not be found anymore. The original message may have been deleted or moved."
            }
            else -> {
                "We couldn't $action in Gmail right now. Please try again in a moment."
            }
        }
        return "$summary\n\n${GoogleApiDiagnosticFormatter.build("Gmail", action, error)}"
    }
}
