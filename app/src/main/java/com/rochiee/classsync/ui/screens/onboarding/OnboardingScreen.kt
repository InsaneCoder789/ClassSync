package com.rochiee.classsync.ui.screens.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.R
import com.rochiee.classsync.bloc.auth.AuthEvent
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.settings.SettingsEvent
import com.rochiee.classsync.bloc.settings.SettingsState
import com.rochiee.classsync.ui.components.ButtonArrowBadge
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun OnboardingScreen(
    authState: AuthUiState,
    settingsState: SettingsState,
    onAuthEvent: (AuthEvent) -> Unit,
    onSettingsEvent: (SettingsEvent) -> Unit,
    onOpenNotificationAccess: () -> Unit,
    onRequestReminderPermissionExplained: () -> Unit,
    onComplete: () -> Unit
) {
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    val steps = remember {
        listOf(
            OnboardingStepArt(R.drawable.onboarding_opening),
            OnboardingStepArt(R.drawable.onboarding_google_connect),
            OnboardingStepArt(R.drawable.onboarding_classroom_access),
            OnboardingStepArt(R.drawable.onboarding_notification_access),
            OnboardingStepArt(R.drawable.onboarding_reminder_setup),
            OnboardingStepArt(R.drawable.onboarding_sync_setup),
            OnboardingStepArt(R.drawable.onboarding_ready)
        )
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onRequestReminderPermissionExplained()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (step) {
            0 -> FullScreenOnboardingStep(
                art = steps[0],
                step = 0,
                totalSteps = steps.size
            ) {
                OnboardingWhiteButton(label = "Continue", onClick = { step += 1 })
            }
            1 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_google_connect),
                step = 1,
                totalSteps = steps.size,
                infoText = null
            ) {
                OnboardingWhiteButton(
                    label = if (authState.isSignedIn) "Continue" else "Continue with Google",
                    onClick = {
                        if (!authState.isSignedIn) {
                            onAuthEvent(AuthEvent.SignIn(context))
                        }
                        step += 1
                    }
                )
            }
            2 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_classroom_access),
                step = 2,
                totalSteps = steps.size
            ) {
                OnboardingWhiteButton(
                    label = "Continue",
                    onClick = {
                        onSettingsEvent(SettingsEvent.SetClassroomPermissionExplained(true))
                        step += 1
                    }
                )
            }
            3 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_notification_access),
                step = 3,
                totalSteps = steps.size
            ) {
                OnboardingWhiteButton(
                    label = "Open access settings",
                    onClick = {
                        onOpenNotificationAccess()
                        onSettingsEvent(SettingsEvent.SetNotificationPermissionExplained(true))
                        step += 1
                    }
                )
            }
            4 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_reminder_setup),
                step = 4,
                totalSteps = steps.size
            ) {
                OnboardingWhiteButton(
                    label = "Allow reminders",
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onRequestReminderPermissionExplained()
                        }
                        step += 1
                    }
                )
            }
            5 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_sync_setup),
                step = 5,
                totalSteps = steps.size,
                infoText = null
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    OnboardingWhiteButton(
                        label = "Keep Gmail off (recommended)",
                        onClick = {
                            onSettingsEvent(SettingsEvent.SetGmailSyncEnabled(false))
                            onSettingsEvent(SettingsEvent.SetGmailPermissionExplained(true))
                            step += 1
                        }
                    )
                    OnboardingWhiteButton(
                        label = if (settingsState.gmailSyncEnabled) "Gmail already enabled" else "Enable Gmail too",
                        onClick = {
                            onSettingsEvent(SettingsEvent.SetGmailSyncEnabled(true))
                            onSettingsEvent(SettingsEvent.SetGmailPermissionExplained(true))
                            step += 1
                        },
                        selected = settingsState.gmailSyncEnabled
                    )
                }
            }
            else -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_ready),
                step = 6,
                totalSteps = steps.size
            ) {
                OnboardingWhiteButton(
                    label = "Open ClassSync",
                    onClick = {
                        onSettingsEvent(SettingsEvent.SetOnboardingCompleted(true))
                        onComplete()
                    }
                )
            }
        }
    }
}

@Composable
private fun FullScreenOnboardingStep(
    art: OnboardingStepArt,
    step: Int,
    totalSteps: Int,
    infoText: String? = null,
    bottomContent: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(id = art.imageRes),
            contentDescription = "Onboarding artwork ${step + 1}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.10f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.22f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                StepBadge(step = step, totalSteps = totalSteps)
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                infoText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.96f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                }
                bottomContent()
            }
        }
    }
}

@Composable
private fun StepBadge(step: Int, totalSteps: Int) {
    Text(
        text = "Step ${step + 1} of $totalSteps",
        style = MaterialTheme.typography.labelLarge,
        color = Color.White.copy(alpha = 0.96f),
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun OnboardingWhiteButton(
    label: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = if (selected) Color(0xFFF4F8FF) else Color.White.copy(alpha = 0.98f),
        tonalElevation = 0.dp,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF14213D),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
            ButtonArrowBadge()
        }
    }
}

private data class OnboardingStepArt(
    val imageRes: Int
)
