package com.rochiee.classsync.data.remote.gmail

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import com.rochiee.classsync.auth.AuthTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GmailApiClient(private val authTokenProvider: AuthTokenProvider) {
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val transport = GoogleNetHttpTransport.newTrustedTransport()

    private suspend fun getGmailService(): Gmail? {
        val credential = authTokenProvider.getGoogleAccountCredential() ?: return null

        return Gmail.Builder(transport, jsonFactory, credential)
            .setApplicationName("ClassSync")
            .build()
    }

    suspend fun searchMessages(query: String): List<Message> = withContext(Dispatchers.IO) {
        val service = getGmailService()
            ?: throw IllegalStateException("Gmail access is unavailable. Connect a Google account and approve Gmail access on this device.")
        try {
            val response = service.users().messages().list("me")
                .setQ(query)
                .setMaxResults(20L)
                .execute()

            response.messages ?: emptyList()
        } catch (e: Exception) {
            throw IllegalStateException(
                GmailErrorInterpreter.toUserMessage(
                    error = e,
                    action = "read your Gmail reminders",
                    accountEmail = authTokenProvider.selectedAccountEmail()
                ),
                e
            )
        }
    }

    suspend fun getMessageDetails(messageId: String): Message? = withContext(Dispatchers.IO) {
        val service = getGmailService()
            ?: throw IllegalStateException("Gmail access is unavailable. Connect a Google account and approve Gmail access on this device.")
        try {
            service.users().messages().get("me", messageId)
                .setFormat("full")
                .execute()
        } catch (e: Exception) {
            throw IllegalStateException(
                GmailErrorInterpreter.toUserMessage(
                    error = e,
                    action = "read that Gmail message",
                    accountEmail = authTokenProvider.selectedAccountEmail()
                ),
                e
            )
        }
    }
}
