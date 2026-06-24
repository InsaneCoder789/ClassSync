package com.rochiee.classsync.ui.screens.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.StatRow
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun AuthDiagnosticsScreen(
    authState: AuthUiState
) {
    val spacing = LocalSpacing.current
    ScreenSection(
        title = "Auth diagnostics",
        subtitle = "Developer checks for OAuth readiness and sign-in state."
    ) {
        TintedPanel {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                StatRow("OAuth configured", if (authState.isOAuthConfigured) "Yes" else "No")
                StatRow("Signed in", if (authState.isSignedIn) "Yes" else "No")
                StatRow("User", authState.userEmail ?: authState.displayName ?: "Not connected")
                StatRow("Status", authState.errorMessage ?: "No current auth error")
                StatRow("Setup doc", "docs/GOOGLE_SETUP.md")
            }
        }
    }
}
