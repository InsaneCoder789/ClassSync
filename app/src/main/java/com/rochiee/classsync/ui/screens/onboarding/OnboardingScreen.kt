package com.rochiee.classsync.ui.screens.onboarding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
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
import com.rochiee.classsync.bloc.sync.SyncEvent
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun OnboardingScreen(
    authState: AuthUiState,
    settingsState: SettingsState,
    syncState: SyncState,
    onBeginGoogleSignIn: (Context) -> Intent?,
    onCompleteGoogleSignIn: (Intent?) -> Unit,
    onAuthEvent: (AuthEvent) -> Unit,
    onSyncEvent: (SyncEvent) -> Unit,
    onSettingsEvent: (SettingsEvent) -> Unit,
    onComplete: () -> Unit
) {
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    var pendingAction by remember { mutableStateOf<OnboardingPendingAction?>(null) }
    var observedSyncStart by remember { mutableStateOf(false) }
    val steps = remember {
        listOf(
            OnboardingStepArt(R.drawable.onboarding_opening),
            OnboardingStepArt(R.drawable.onboarding_google_connect),
            OnboardingStepArt(R.drawable.onboarding_classroom_access),
            OnboardingStepArt(R.drawable.onboarding_reminder_setup),
            OnboardingStepArt(R.drawable.onboarding_sync_setup),
            OnboardingStepArt(R.drawable.onboarding_ready)
        )
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            step = 4
        }
    }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        onCompleteGoogleSignIn(result.data)
    }

    LaunchedEffect(authState.isSignedIn, authState.isLoading, authState.errorMessage, pendingAction) {
        if (pendingAction == OnboardingPendingAction.Auth && authState.isSignedIn) {
            pendingAction = null
            step = 2
        } else if (pendingAction == OnboardingPendingAction.Auth && !authState.isLoading && authState.errorMessage != null) {
            pendingAction = null
        } else if (pendingAction == OnboardingPendingAction.Auth && !authState.isLoading && !authState.isSignedIn) {
            pendingAction = null
        }
    }

    LaunchedEffect(syncState.isSyncing, syncState.errorMessage, pendingAction, observedSyncStart) {
        if (pendingAction == OnboardingPendingAction.ClassroomSync || pendingAction == OnboardingPendingAction.GmailSync) {
            if (syncState.isSyncing) {
                observedSyncStart = true
            } else if (observedSyncStart) {
                val failed = !syncState.errorMessage.isNullOrBlank()
                if (!failed) {
                    step += 1
                }
                pendingAction = null
                observedSyncStart = false
            }
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
                infoText = authState.errorMessage
            ) {
                OnboardingWhiteButton(
                    label = when {
                        authState.isSignedIn -> "Connected"
                        pendingAction == OnboardingPendingAction.Auth || authState.isLoading -> "Connecting..."
                        else -> "Continue with Google"
                    },
                    onClick = {
                        if (authState.isSignedIn) {
                            step = 2
                        } else {
                            pendingAction = OnboardingPendingAction.Auth
                            onAuthEvent(AuthEvent.ClearAuthError)
                            onBeginGoogleSignIn(context)?.let(googleSignInLauncher::launch)
                        }
                    },
                    enabled = !authState.isLoading
                )
            }
            2 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_classroom_access),
                step = 2,
                totalSteps = steps.size,
                infoText = syncState.errorMessage
            ) {
                OnboardingWhiteButton(
                    label = if (pendingAction == OnboardingPendingAction.ClassroomSync || syncState.isSyncing) {
                        "Syncing Classroom..."
                    } else {
                        "Connect Classroom"
                    },
                    onClick = {
                        onSettingsEvent(SettingsEvent.SetClassroomPermissionExplained(true))
                        onSettingsEvent(SettingsEvent.SetClassroomSyncEnabled(true))
                        pendingAction = OnboardingPendingAction.ClassroomSync
                        observedSyncStart = false
                        onSyncEvent(SyncEvent.ClearError)
                        onSyncEvent(SyncEvent.RunClassroomSync)
                    },
                    enabled = !syncState.isSyncing
                )
            }
            3 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_reminder_setup),
                step = 3,
                totalSteps = steps.size
            ) {
                OnboardingWhiteButton(
                    label = "Allow reminders",
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            step = 4
                        }
                    }
                )
            }
            4 -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_sync_setup),
                step = 4,
                totalSteps = steps.size,
                infoText = syncState.errorMessage
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    OnboardingWhiteButton(
                        label = "Keep Gmail off",
                        onClick = {
                            onSettingsEvent(SettingsEvent.SetGmailSyncEnabled(false))
                            onSettingsEvent(SettingsEvent.SetGmailPermissionExplained(true))
                            step = 5
                        },
                        variant = OnboardingButtonVariant.Secondary
                    )
                    OnboardingWhiteButton(
                        label = if (pendingAction == OnboardingPendingAction.GmailSync || syncState.isSyncing) {
                            "Syncing Gmail..."
                        } else if (settingsState.gmailSyncEnabled) {
                            "Enable Gmail too"
                        } else {
                            "Enable Gmail too"
                        },
                        onClick = {
                            onSettingsEvent(SettingsEvent.SetGmailSyncEnabled(true))
                            onSettingsEvent(SettingsEvent.SetGmailPermissionExplained(true))
                            pendingAction = OnboardingPendingAction.GmailSync
                            observedSyncStart = false
                            onSyncEvent(SyncEvent.ClearError)
                            onSyncEvent(SyncEvent.RunGmailSync)
                        },
                        selected = settingsState.gmailSyncEnabled,
                        enabled = !syncState.isSyncing
                    )
                }
            }
            else -> FullScreenOnboardingStep(
                art = OnboardingStepArt(R.drawable.onboarding_ready),
                step = 5,
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

private enum class OnboardingPendingAction {
    Auth,
    ClassroomSync,
    GmailSync
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.Black.copy(alpha = 0.34f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.97f)
                        )
                    }
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
    selected: Boolean = false,
    enabled: Boolean = true,
    variant: OnboardingButtonVariant = OnboardingButtonVariant.Primary,
    showArrow: Boolean = true
) {
    val buttonShape = RoundedCornerShape(22.dp)
    val arrowShape = RoundedCornerShape(15.dp)
    val isPrimary = variant == OnboardingButtonVariant.Primary
    val isLightMode = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val shellGradient = if (isPrimary) {
        listOf(
            Color.White.copy(alpha = if (enabled) 0.34f else 0.18f),
            Color(0xFFDCE8FA).copy(alpha = if (enabled) 0.24f else 0.12f)
        )
    } else {
        listOf(
            Color.White.copy(alpha = if (enabled) 0.22f else 0.12f),
            Color(0xFFC7D7F0).copy(alpha = if (enabled) 0.14f else 0.08f)
        )
    }
    val borderColor = if (isPrimary) {
        Color.White.copy(alpha = if (enabled) 0.42f else 0.20f)
    } else {
        Color.White.copy(alpha = if (enabled) 0.28f else 0.14f)
    }
    val textColor = if (enabled) Color.White.copy(alpha = if (isPrimary) 0.98f else 0.92f) else Color.White.copy(alpha = 0.52f)
    val arrowContainerColor = if (isLightMode) {
        if (isPrimary) Color.White.copy(alpha = 0.82f) else Color.White.copy(alpha = 0.68f)
    } else {
        if (isPrimary) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.16f)
    }
    val arrowTint = if (isLightMode) Color(0xFF173055) else Color.White
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 74.dp),
        shape = buttonShape,
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = buttonShape
                )
                .background(
                    brush = Brush.verticalGradient(
                        shellGradient
                    ),
                    shape = buttonShape
                )
                .padding(start = 24.dp, end = 14.dp, top = 15.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
            Surface(
                modifier = Modifier.size(42.dp),
                shape = arrowShape,
                color = arrowContainerColor,
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = arrowTint,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

private enum class OnboardingButtonVariant {
    Primary,
    Secondary
}

private data class OnboardingStepArt(
    val imageRes: Int
)
