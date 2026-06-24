package com.rochiee.classsync.auth

import android.content.Context
import androidx.credentials.CustomCredential
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.rochiee.classsync.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoogleAuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val secureAuthStore = SecureAuthStore(context.applicationContext)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val webClientId: String
        get() = BuildConfig.GOOGLE_WEB_CLIENT_ID.trim()

    suspend fun signIn(activityContext: Context) {
        _authState.value = AuthState.Loading
        try {
            if (!isOAuthConfigured()) {
                _authState.value = AuthState.Error(
                    "Google OAuth is not configured yet. Replace the placeholder client ID and follow docs/GOOGLE_SETUP.md before signing in."
                )
                return
            }
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                secureAuthStore.saveSession(
                    email = googleCredential.id,
                    displayName = googleCredential.displayName
                )
                _authState.value = AuthState.Authenticated(
                    email = googleCredential.id,
                    displayName = googleCredential.displayName,
                    idToken = googleCredential.idToken
                )
            } else {
                _authState.value = AuthState.Error("Unexpected credential type")
            }
        } catch (_: NoCredentialException) {
            _authState.value = AuthState.Error("No Google credential was selected.")
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            secureAuthStore.clearSession()
            _authState.value = AuthState.Unauthenticated
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign-out failed")
        }
    }

    fun checkAuthState() {
        if (_authState.value == AuthState.Idle) {
            val session = secureAuthStore.restoreSession()
            _authState.value = if (isOAuthConfigured() && session != null) {
                AuthState.Authenticated(
                    email = session.email,
                    displayName = session.displayName
                )
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    fun isOAuthConfigured(): Boolean {
        return webClientId.isNotBlank() &&
            !webClientId.startsWith("YOUR_WEB_CLIENT_ID") &&
            webClientId.endsWith(".apps.googleusercontent.com")
    }

    fun isSignedIn(): Boolean = authState.value is AuthState.Authenticated

    fun selectedAccountEmail(): String? {
        return (authState.value as? AuthState.Authenticated)?.email
            ?: secureAuthStore.restoreSession()?.email
    }
}
