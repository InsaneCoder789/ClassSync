package com.rochiee.classsync.data.remote.classroom

import com.google.api.client.googleapis.json.GoogleJsonResponseException

object ClassroomErrorInterpreter {
    fun toUserMessage(error: Throwable, action: String): String {
        val googleError = error as? GoogleJsonResponseException
        val statusCode = googleError?.statusCode
        val rawMessage = googleError?.details?.message
            ?: error.message
            ?: "Classroom request failed."
        val normalized = rawMessage.lowercase()

        return when {
            statusCode == 401 -> {
                "Your Google Classroom session expired. Sign in again, then retry $action."
            }
            statusCode == 403 && "permission" in normalized -> {
                "Classroom access was denied for this account. If you are using a school account like @kiit.ac.in, your Google Workspace admin may need to allow Classroom API access for third-party apps before ClassSync can $action."
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
