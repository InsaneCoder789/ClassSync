package com.rochiee.classsync.ui.screens.auth

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.ui.components.AppLogoLockup
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun AuthScreen(
    authState: AuthUiState,
    syncState: SyncState,
    onBeginGoogleSignIn: (Context) -> Intent?,
    onCompleteGoogleSignIn: (Intent?) -> Unit,
    onAuthEvent: (AuthEvent) -> Unit,
    onSyncEvent: (SyncEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        onCompleteGoogleSignIn(result.data)
    }
    Column(
        modifier = Modifier.padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        TintedPanel {
            AppLogoLockup(subtitle = "Academic sync with clear privacy boundaries")
            Text(
                text = "Connect the account that already carries your classes.",
                style = MaterialTheme.typography.headlineMedium
            )
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
                        text = if (authState.isGoogleAccessHealthy) {
                            "Your Google account is connected. Classroom sync is ready, and Gmail sync stays opt-in through settings."
                        } else {
                            "Your account is still remembered on this device. ClassSync will keep trying sync with this account, and you can refresh Google sign-in if Google asks for it."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (!authState.isGoogleAccessHealthy) {
                    LiquidGlassTextButton(
                        text = "Refresh Google connection",
                        onClick = {
                            onAuthEvent(AuthEvent.ClearAuthError)
                            onBeginGoogleSignIn(context)?.let(signInLauncher::launch)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authState.isOAuthConfigured,
                        showArrow = true
                    )
                }
                LiquidGlassTextButton(
                    text = if (syncState.isSyncing) "Syncing..." else "Sync Classroom",
                    onClick = { onSyncEvent(SyncEvent.RunClassroomSync) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !syncState.isSyncing,
                    showArrow = true
                )
                LiquidGlassTextButton(
                    text = if (syncState.isSyncing) "Syncing..." else "Sync Gmail",
                    onClick = { onSyncEvent(SyncEvent.RunGmailSync) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !syncState.isSyncing,
                    showArrow = true
                )
                LiquidGlassTextButton(text = "Sign out", onClick = { onAuthEvent(AuthEvent.SignOut) })
            } else {
                Text(
                    text = "Sign in to connect your Google identity and enable Classroom plus optional Gmail sync.",
                    style = MaterialTheme.typography.bodyLarge
                )
                LiquidGlassTextButton(
                    text = "Continue with Google",
                    onClick = {
                        onAuthEvent(AuthEvent.ClearAuthError)
                        onBeginGoogleSignIn(context)?.let(signInLauncher::launch)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState.isOAuthConfigured,
                    showArrow = true
                )
            }
            authState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            authState.accessWarning?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            syncState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
