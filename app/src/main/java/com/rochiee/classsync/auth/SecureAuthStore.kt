package com.rochiee.classsync.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureAuthStore(context: Context) {

    private val preferences: SharedPreferences = runCatching {
        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context.applicationContext,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrElse {
        // Persist the selected Google identity even if encrypted preferences are unavailable
        // on a specific device build. The app only stores the chosen email/display name here.
        context.applicationContext.getSharedPreferences(FILE_NAME_FALLBACK, Context.MODE_PRIVATE)
    }

    fun saveSession(email: String, displayName: String?) {
        preferences?.edit()
            ?.putString(KEY_EMAIL, email)
            ?.putString(KEY_DISPLAY_NAME, displayName)
            ?.commit()
    }

    fun restoreSession(): PersistedAuthSession? {
        val email = preferences.getString(KEY_EMAIL, null)?.trim().orEmpty()
        if (email.isBlank()) return null

        return PersistedAuthSession(
            email = email,
            displayName = preferences.getString(KEY_DISPLAY_NAME, null)
        )
    }

    fun clearSession() {
        preferences.edit().clear().commit()
    }

    data class PersistedAuthSession(
        val email: String,
        val displayName: String?
    )

    private companion object {
        const val FILE_NAME = "classsync_secure_auth"
        const val FILE_NAME_FALLBACK = "classsync_auth_fallback"
        const val KEY_EMAIL = "google_account_email"
        const val KEY_DISPLAY_NAME = "google_account_display_name"
    }
}
