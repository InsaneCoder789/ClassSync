package com.rochiee.classsync.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Tasks
import com.rochiee.classsync.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class GoogleAuthManager(private val context: Context) {
    private val secureAuthStore = SecureAuthStore(context.applicationContext)
    private val requiredScopes: Array<Scope>
        get() = GoogleScopes.ALL_SCOPES.map(::Scope).toTypedArray()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val webClientId: String
        get() = BuildConfig.GOOGLE_WEB_CLIENT_ID.trim()

    fun beginSignInIntent(activityContext: Context): Intent? {
        if (!isOAuthConfigured()) {
            _authState.value = AuthState.Error(
                "Google OAuth is not configured yet. Complete the Google setup guide for this build before signing in."
            )
            return null
        }

        _authState.value = AuthState.Loading
        return googleSignInClient(activityContext).signInIntent
    }

    fun completeSignIn(data: Intent?) {
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
            persistAccount(account)
        } catch (error: ApiException) {
            _authState.value = when (error.statusCode) {
                CommonStatusCodes.CANCELED -> AuthState.Error(
                    "Google sign-in was canceled before an account was selected."
                )
                CommonStatusCodes.DEVELOPER_ERROR -> AuthState.Error(
                    "Google sign-in is misconfigured for this build. Recheck the Android OAuth client package name plus SHA-1/SHA-256 fingerprints."
                )
                GoogleSignInStatusCodes.SIGN_IN_FAILED -> AuthState.Error(
                    "Google sign-in failed for this build. This usually means the Android OAuth client setup is incomplete or mismatched. Recheck the package name, SHA-1, and SHA-256 fingerprints in Google Cloud, then rebuild and try again."
                )
                GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> AuthState.Error(
                    "Google sign-in is already in progress. Wait a moment and try again."
                )
                else -> AuthState.Error(
                    buildString {
                        append("Google sign-in failed: ")
                        append(
                            GoogleSignInStatusCodes.getStatusCodeString(error.statusCode)
                                .takeIf { it.isNotBlank() && it != "unknown status code" }
                                ?: CommonStatusCodes.getStatusCodeString(error.statusCode)
                        )
                        append(" (code ")
                        append(error.statusCode)
                        append(")")
                        error.localizedMessage?.takeIf { it.isNotBlank() }?.let {
                            append(". ")
                            append(it)
                        }
                    }
                )
            }
        } catch (error: Exception) {
            _authState.value = AuthState.Error(error.message ?: "Google sign-in failed.")
        }
    }

    suspend fun signOut(): Boolean {
        var completedRemoteSignOut = true
        try {
            withContext(Dispatchers.IO) {
                Tasks.await(googleSignInClient(context).signOut())
            }
        } catch (e: Exception) {
            completedRemoteSignOut = false
        } finally {
            secureAuthStore.clearSession()
            _authState.value = AuthState.Unauthenticated
        }
        return completedRemoteSignOut
    }

    suspend fun checkAuthState() {
        if (_authState.value != AuthState.Idle) return
        if (!isOAuthConfigured()) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        val account = GoogleSignIn.getLastSignedInAccount(context)
        val session = secureAuthStore.restoreSession()
        _authState.value = when {
            account?.email?.isNotBlank() == true && hasRequiredPermissions(account) -> {
                persistAccount(account)
                _authState.value
            }
            account?.email?.isNotBlank() == true -> {
                secureAuthStore.clearSession()
                AuthState.Error(
                    "Google sign-in is missing the Gmail or Classroom permissions ClassSync needs. Sign out, sign in again, and accept every requested Google permission."
                )
            }
            session != null -> {
                recoverPersistedSession(session)
                AuthState.Authenticated(
                    email = session.email,
                    displayName = session.displayName
                )
            }
            else -> AuthState.Unauthenticated
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

    private fun googleSignInClient(activityContext: Context) =
        GoogleSignIn.getClient(activityContext, googleSignInOptions())

    private suspend fun recoverPersistedSession(session: SecureAuthStore.PersistedAuthSession) {
        runCatching {
            withContext(Dispatchers.IO) {
                Tasks.await(googleSignInClient(context).silentSignIn())
            }
        }.onSuccess { account ->
            if (account.email?.isNotBlank() == true && hasRequiredPermissions(account)) {
                persistAccount(account)
            } else {
                // Keep the remembered identity until the user explicitly signs out.
                _authState.value = AuthState.Authenticated(
                    email = session.email,
                    displayName = session.displayName
                )
            }
        }.onFailure {
            _authState.value = AuthState.Authenticated(
                email = session.email,
                displayName = session.displayName
            )
        }
    }

    private fun googleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(webClientId)
            .requestScopes(requiredScopes.first(), *requiredScopes.drop(1).toTypedArray())
            .build()
    }

    private fun hasRequiredPermissions(account: GoogleSignInAccount): Boolean {
        return GoogleSignIn.hasPermissions(account, *requiredScopes)
    }

    private fun persistAccount(account: GoogleSignInAccount) {
        val email = account.email?.trim().orEmpty()
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Google sign-in succeeded but no account email was returned.")
            return
        }

        secureAuthStore.saveSession(
            email = email,
            displayName = account.displayName
        )
        _authState.value = AuthState.Authenticated(
            email = email,
            displayName = account.displayName,
            idToken = account.idToken
        )
    }
}
