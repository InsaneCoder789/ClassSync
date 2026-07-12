package com.rochiee.classsync.auth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(
        val email: String,
        val displayName: String?,
        val idToken: String? = null,
        val accessStatus: GoogleAccessStatus = GoogleAccessStatus.Healthy,
        val accessMessage: String? = null
    ) : AuthState()
    data class Error(val message: String) : AuthState()
    object Unauthenticated : AuthState()
}

/**
 * Google access may expire independently of the locally remembered account.
 * A non-healthy status must never be treated as an implicit logout.
 */
enum class GoogleAccessStatus {
    Healthy,
    NeedsReauth
}
