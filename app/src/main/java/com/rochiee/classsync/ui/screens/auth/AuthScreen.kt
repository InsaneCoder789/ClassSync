package com.rochiee.classsync.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.auth.AuthEvent
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.sync.SyncEvent
import com.rochiee.classsync.ui.components.AppLogoLockup
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun AuthScreen(
    authState: AuthUiState,
    onAuthEvent: (AuthEvent) -> Unit,
    onSyncEvent: (SyncEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(
        modifier = Modifier.padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        TintedPanel {
            AppLogoLockup(subtitle = "Academic sync with clear privacy boundaries")
            Text(
                text = if (authState.isOAuthConfigured) {
                    "Connect your Google account to unlock Classroom sync and optional Gmail reminder sync on this device."
                } else {
                    "Google sync needs a local OAuth client ID before sign-in can begin. Follow docs/GOOGLE_SETUP.md on this machine."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ScreenSection(
            title = "Google account",
            subtitle = "Classroom is the primary academic source. Gmail remains optional."
        ) {
            if (authState.isSignedIn) {
                EmptyState(
                    title = authState.displayName ?: "Connected",
                    description = authState.userEmail ?: "Your Google account is active."
                )
                TintedPanel {
                    Text(
                        text = "Connection status",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Your Google account is connected. Classroom sync is ready, and Gmail sync stays opt-in through settings.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                LiquidGlassTextButton(
                    text = "Sync Classroom",
                    onClick = { onSyncEvent(SyncEvent.RunClassroomSync) },
                    modifier = Modifier.fillMaxWidth()
                )
                LiquidGlassTextButton(
                    text = "Sync Gmail",
                    onClick = { onSyncEvent(SyncEvent.RunGmailSync) },
                    modifier = Modifier.fillMaxWidth()
                )
                LiquidGlassTextButton(text = "Sign out", onClick = { onAuthEvent(AuthEvent.SignOut) })
            } else {
                Text(
                    text = "Sign in to connect your Google identity and enable Classroom plus optional Gmail sync.",
                    style = MaterialTheme.typography.bodyLarge
                )
                LiquidGlassTextButton(
                    text = "Continue with Google",
                    onClick = { onAuthEvent(AuthEvent.SignIn(context)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState.isOAuthConfigured
                )
            }
            authState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
