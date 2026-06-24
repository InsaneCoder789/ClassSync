package com.rochiee.classsync.auth

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

class AuthTokenProvider(
    private val context: Context,
    private val googleAuthManager: GoogleAuthManager
) {
    fun getGoogleAccountCredential(): GoogleAccountCredential? {
        val email = googleAuthManager.selectedAccountEmail() ?: return null
        return GoogleAccountCredential
            .usingOAuth2(context.applicationContext, GoogleScopes.ALL_SCOPES)
            .setSelectedAccountName(email)
    }
}
