package com.rochiee.classsync.ui.screens.settings

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.settings.SettingsEvent
import com.rochiee.classsync.bloc.settings.SettingsState
import com.rochiee.classsync.bloc.sync.SyncEvent
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.ui.components.AppLogoLockup
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settingsState: SettingsState,
    authState: AuthUiState,
    syncState: SyncState,
    onSettingsEvent: (SettingsEvent) -> Unit,
    onSyncEvent: (SyncEvent) -> Unit,
    onNavigateToDebug: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .padding(spacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        TintedPanel {
            AppLogoLockup(subtitle = "Privacy-first controls and local-first automation")
            ResponsiveFlowRow(maxItemsInEachRow = 1) {
                ElevatedInfoCard(
                    title = "Theme",
                    value = settingsState.themeMode.name.lowercase().replaceFirstChar { it.uppercase() },
                    supportingText = "Current visual mode",
                    modifier = Modifier.fillMaxWidth(),
                    accent = SkyBlue
                )
                ElevatedInfoCard(
                    title = "Reminder",
                    value = "${settingsState.defaultReminderHours}h",
                    supportingText = "Lead time before deadlines",
                    modifier = Modifier.fillMaxWidth(),
                    accent = Sun
                )
                ElevatedInfoCard(
                    title = "Digest",
                    value = if (settingsState.digestEnabled) "On" else "Off",
                    supportingText = "Daily overview delivery",
                    modifier = Modifier.fillMaxWidth(),
                    accent = MintGreen
                )
            }
        }

        ScreenSection(title = "Settings", subtitle = "Privacy-first controls for sync, reminders, and onboarding.") {
            SettingsToggleRow(
                title = "Classroom sync",
                description = "Primary source for assignments, quizzes, and course activity.",
                checked = settingsState.classroomSyncEnabled,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetClassroomSyncEnabled(it)) }
            )
            SettingsToggleRow(
                title = "Gmail sync (optional)",
                description = "Gmail sync is optional and only used to find academic reminders. ClassSync does not upload your emails to any server.",
                checked = settingsState.gmailSyncEnabled,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetGmailSyncEnabled(it)) }
            )
            SettingsToggleRow(
                title = "Notification parsing",
                description = "Allow notification-based local task/event extraction.",
                checked = settingsState.notificationParsingEnabled,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetNotificationParsingEnabled(it)) }
            )
            SettingsToggleRow(
                title = "Background sync",
                description = "Enable WorkManager-based periodic sync jobs.",
                checked = settingsState.backgroundSyncEnabled,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetBackgroundSyncEnabled(it)) }
            )
        }

        ScreenSection(title = "Appearance", subtitle = "Pick the mode that feels best on your device.") {
            TintedPanel {
                Text(
                    text = "Theme mode",
                    style = MaterialTheme.typography.titleMedium
                )
                ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    ThemeModeButton(
                        label = "Light",
                        selected = settingsState.themeMode == ThemeMode.LIGHT,
                        onClick = { onSettingsEvent(SettingsEvent.SetThemeMode(ThemeMode.LIGHT)) },
                        modifier = Modifier.widthIn(min = 132.dp)
                    )
                    ThemeModeButton(
                        label = "Dark",
                        selected = settingsState.themeMode == ThemeMode.DARK,
                        onClick = { onSettingsEvent(SettingsEvent.SetThemeMode(ThemeMode.DARK)) },
                        modifier = Modifier.widthIn(min = 132.dp)
                    )
                }
            }
        }

        TintedPanel {
            Text(text = "Reminder lead time: ${settingsState.defaultReminderHours}h", style = MaterialTheme.typography.titleMedium)
            ResponsiveFlowRow(maxItemsInEachRow = 3) {
                LiquidGlassTextButton(text = "1h", onClick = { onSettingsEvent(SettingsEvent.SetDefaultReminderHours(1)) }, modifier = Modifier.widthIn(min = 84.dp), selected = settingsState.defaultReminderHours == 1)
                LiquidGlassTextButton(text = "2h", onClick = { onSettingsEvent(SettingsEvent.SetDefaultReminderHours(2)) }, modifier = Modifier.widthIn(min = 84.dp), selected = settingsState.defaultReminderHours == 2)
                LiquidGlassTextButton(text = "6h", onClick = { onSettingsEvent(SettingsEvent.SetDefaultReminderHours(6)) }, modifier = Modifier.widthIn(min = 84.dp), selected = settingsState.defaultReminderHours == 6)
            }
            Text(text = "Last sync: ${settingsState.lastSyncTimeMillis.formatDateTime()}", style = MaterialTheme.typography.bodyMedium)
            if (authState.isSignedIn) {
                Text(
                    text = "Connected as ${authState.userEmail ?: authState.displayName ?: "student"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Google identity is not connected yet.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = if (authState.isOAuthConfigured) {
                    "Classroom sync is available once a Google account is connected. Gmail sync remains optional and fully local-first."
                } else {
                    "Google sync is disabled until a local OAuth client ID is supplied on this machine."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            syncState.errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            ResponsiveFlowRow(maxItemsInEachRow = 2) {
                LiquidGlassTextButton(
                    text = if (syncState.isSyncing) "Syncing..." else "Sync Classroom",
                    onClick = { onSyncEvent(SyncEvent.RunClassroomSync) },
                    modifier = Modifier.widthIn(min = 148.dp),
                    enabled = authState.isSignedIn && settingsState.classroomSyncEnabled && !syncState.isSyncing
                )
                LiquidGlassTextButton(
                    text = if (syncState.isSyncing) "Syncing..." else "Sync Gmail",
                    onClick = { onSyncEvent(SyncEvent.RunGmailSync) },
                    modifier = Modifier.widthIn(min = 148.dp),
                    enabled = authState.isSignedIn && settingsState.gmailSyncEnabled && !syncState.isSyncing
                )
            }
            ResponsiveFlowRow(maxItemsInEachRow = 2) {
                LiquidGlassTextButton(
                    text = if (syncState.isSyncing) "Syncing..." else "Full Sync",
                    onClick = { onSyncEvent(SyncEvent.RunManualFullSync) },
                    modifier = Modifier.widthIn(min = 148.dp),
                    enabled = authState.isSignedIn && !syncState.isSyncing
                )
                LiquidGlassTextButton(text = "Debug tools", onClick = onNavigateToDebug, modifier = Modifier.widthIn(min = 148.dp))
                if (!authState.isSignedIn) {
                    LiquidGlassTextButton(text = "Connect Google", onClick = onNavigateToAuth, modifier = Modifier.widthIn(min = 148.dp))
                }
            }
        }

        ScreenSection(title = "Daily digest", subtitle = "A once-a-day local digest of due work, exams, and course updates.") {
            SettingsToggleRow(
                title = "Enable daily digest",
                description = "Receive a short academic overview each day.",
                checked = settingsState.digestEnabled,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetDigestEnabled(it)) }
            )
            SettingsToggleRow(
                title = "Include announcements",
                description = "Add the latest announcement headlines to the digest.",
                checked = settingsState.digestIncludeAnnouncements,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetDigestIncludeAnnouncements(it)) }
            )
            SettingsToggleRow(
                title = "Include materials",
                description = "Add recent study materials to the digest.",
                checked = settingsState.digestIncludeMaterials,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetDigestIncludeMaterials(it)) }
            )
            TintedPanel {
                Text(text = "Digest time: ${settingsState.digestHourOfDay}:00", style = MaterialTheme.typography.titleMedium)
                ResponsiveFlowRow(maxItemsInEachRow = 3) {
                    LiquidGlassTextButton(text = "7 AM", onClick = { onSettingsEvent(SettingsEvent.SetDigestHourOfDay(7)) }, modifier = Modifier.widthIn(min = 92.dp), selected = settingsState.digestHourOfDay == 7)
                    LiquidGlassTextButton(text = "12 PM", onClick = { onSettingsEvent(SettingsEvent.SetDigestHourOfDay(12)) }, modifier = Modifier.widthIn(min = 92.dp), selected = settingsState.digestHourOfDay == 12)
                    LiquidGlassTextButton(text = "8 PM", onClick = { onSettingsEvent(SettingsEvent.SetDigestHourOfDay(20)) }, modifier = Modifier.widthIn(min = 92.dp), selected = settingsState.digestHourOfDay == 20)
                }
                LiquidGlassTextButton(text = "Preview Today's Digest", onClick = { onSettingsEvent(SettingsEvent.PreviewDigest) }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ThemeModeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassTextButton(text = label, onClick = onClick, modifier = modifier, selected = selected)
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    TintedPanel {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.xs)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
