package com.rochiee.classsync.data.remote.classroom

import com.google.api.client.googleapis.json.GoogleJsonResponseException

object ClassroomErrorInterpreter {
    fun toUserMessage(
        error: Throwable,
        action: String,
        accountEmail: String? = null
    ): String {
        val googleError = error as? GoogleJsonResponseException
        val statusCode = googleError?.statusCode
        val rawMessage = googleError?.details?.message
            ?: error.message
            ?: "Classroom request failed."
        val normalized = rawMessage.lowercase()
        val googleReasons = googleError?.details?.errors
            ?.mapNotNull { it.reason?.lowercase() }
            .orEmpty()
        val isPersonalAccount = accountEmail?.substringAfter('@', missingDelimiterValue = "")
            ?.equals("gmail.com", ignoreCase = true) == true
        val signedInLabel = accountEmail?.takeIf { it.isNotBlank() } ?: "this Google account"

        return when {
            statusCode == 401 -> {
                "Your Google Classroom session expired. Sign in again, then retry $action."
            }
            statusCode == 403 && (
                "permission" in normalized ||
                    "permission_denied" in normalized ||
                    "the caller does not have permission" in normalized ||
                    googleReasons.any { reason ->
                        reason.contains("forbidden") ||
                            reason.contains("permission") ||
                            reason.contains("access_denied")
                    }
                ) -> {
                if (isPersonalAccount) {
                    "$signedInLabel signed in successfully, but Google Classroom data is not available for that account. Personal Google accounts can only sync classes they personally have access to. Open classroom.google.com with the same account first. If your classes are only available through your school Workspace account, sign in with that school account in ClassSync too."
                } else {
                    "$signedInLabel signed in successfully, but Google Classroom denied API access for that account. If this is a school-managed Workspace account, the Google Workspace admin may need to allow third-party access to Classroom data before ClassSync can $action."
                }
            }
            statusCode == 403 && (
                "classroomdisabled" in normalized ||
                    "classroom disabled" in normalized ||
                    googleReasons.any { it.contains("classroomdisabled") }
                ) -> {
                "$signedInLabel can't use Google Classroom on this account right now. Check whether Classroom itself opens for the same account at classroom.google.com."
            }
            statusCode == 403 && (
                "not a member" in normalized ||
                    "requested entity was not found" in normalized
                ) -> {
                "$signedInLabel does not have access to one or more of the requested classes. Make sure the same account is enrolled in those classes in Google Classroom."
            }
            statusCode == 403 && "forbidden" in normalized -> {
                "Classroom blocked this request for the current account. Check that this Google account can open the class in Classroom and has access to the requested coursework."
            }
            statusCode == 403 && "api" in normalized && "disabled" in normalized -> {
                "The Classroom API is not enabled for this Google Cloud project yet. Enable it in Google Cloud Console, then try again."
            }
            statusCode == 404 -> {
                "Some Classroom data could not be found anymore. The course or assignment may have been removed."
            }
            else -> {
                "We couldn't $action in Classroom right now. Please try again in a moment."
            }
        }
    }
}
