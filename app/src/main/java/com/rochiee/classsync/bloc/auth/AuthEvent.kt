package com.rochiee.classsync.bloc.auth

sealed class AuthEvent {
    object CheckAuthState : AuthEvent()
    object SignOut : AuthEvent()
    object ClearAuthError : AuthEvent()
}
