package com.rochiee.classsync.bloc.auth

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.auth.AuthState
import com.rochiee.classsync.auth.GoogleAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthBlocViewModel(
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(isOAuthConfigured = googleAuthManager.isOAuthConfigured()) }
        googleAuthManager.authState
            .onEach { authState ->
                _state.update {
                    when (authState) {
                        is AuthState.Authenticated -> it.copy(
                            isOAuthConfigured = googleAuthManager.isOAuthConfigured(),
                            isSignedIn = true,
                            isLoading = false,
                            userEmail = authState.email,
                            displayName = authState.displayName,
                            errorMessage = null
                        )
                        is AuthState.Error -> it.copy(
                            isOAuthConfigured = googleAuthManager.isOAuthConfigured(),
                            isSignedIn = false,
                            isLoading = false,
                            errorMessage = authState.message
                        )
                        AuthState.Loading -> it.copy(
                            isOAuthConfigured = googleAuthManager.isOAuthConfigured(),
                            isLoading = true,
                            errorMessage = null
                        )
                        AuthState.Unauthenticated, AuthState.Idle -> it.copy(
                            isOAuthConfigured = googleAuthManager.isOAuthConfigured(),
                            isSignedIn = false,
                            isLoading = false,
                            userEmail = null,
                            displayName = null,
                            errorMessage = null
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.CheckAuthState -> googleAuthManager.checkAuthState()
            AuthEvent.SignOut -> {
                viewModelScope.launch {
                    googleAuthManager.signOut()
                }
            }
            AuthEvent.ClearAuthError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun beginSignInIntent(context: Context): Intent? = googleAuthManager.beginSignInIntent(context)

    fun completeSignIn(data: Intent?) {
        googleAuthManager.completeSignIn(data)
    }
}
