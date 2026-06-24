package com.rochiee.classsync.data.remote.gmail

import android.util.Base64
import com.google.api.services.gmail.model.MessagePart
import com.google.api.services.gmail.model.MessagePartHeader

class GmailRemoteDataSource(private val apiClient: GmailApiClient) {
    
    suspend fun fetchRecentAcademicMessages(): List<GmailMessageDto> {
        val query = "newer_than:21d (from:(classroom.google.com) OR from:(no-reply@classroom.google.com)) (assignment OR due OR coursework OR quiz OR exam OR reminder OR deadline OR grade)"
        val messages = apiClient.searchMessages(query)

        return messages.take(20).mapNotNull { message ->
            val details = apiClient.getMessageDetails(message.id) ?: return@mapNotNull null
            
            val headers = details.payload.headers
            val subject = getHeader(headers, "Subject")
            val from = getHeader(headers, "From")
            val body = extractBody(details.payload)
            
            GmailMessageDto(
                id = details.id,
                threadId = details.threadId,
                subject = subject,
                from = from,
                snippet = details.snippet,
                body = body,
                internalDateMillis = details.internalDate,
                link = "https://mail.google.com/mail/u/0/#inbox/${details.id}"
            )
        }
    }

    private fun getHeader(headers: List<MessagePartHeader>?, name: String): String? {
        return headers?.find { it.name.equals(name, ignoreCase = true) }?.value
    }

    private fun extractBody(payload: MessagePart): String? {
        if (payload.body.data != null) {
            return String(Base64.decode(payload.body.data, Base64.URL_SAFE or Base64.NO_WRAP))
        }
        
        if (payload.parts != null) {
            for (part in payload.parts) {
                val body = extractBody(part)
                if (body != null) return body
            }
        }
        return null
    }
}
