package com.rochiee.classsync.bloc.auth

data class AuthUiState(
    val isSignedIn: Boolean = false,
    val isGoogleAccessHealthy: Boolean = false,
    val isLoading: Boolean = true,
    val isOAuthConfigured: Boolean = false,
    val userEmail: String? = null,
    val displayName: String? = null,
    val errorMessage: String? = null,
    val accessWarning: String? = null
)
