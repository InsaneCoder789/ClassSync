package com.rochiee.classsync.auth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(
        val email: String,
        val displayName: String?,
        val idToken: String? = null
    ) : AuthState()
    data class Error(val message: String) : AuthState()
    object Unauthenticated : AuthState()
}