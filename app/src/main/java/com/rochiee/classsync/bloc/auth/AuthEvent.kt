package com.rochiee.classsync.bloc.auth

import android.content.Context

sealed class AuthEvent {
    object CheckAuthState : AuthEvent()
    data class SignIn(val context: Context) : AuthEvent()
    object SignOut : AuthEvent()
    object ClearAuthError : AuthEvent()
}