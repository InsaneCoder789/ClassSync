package com.rochiee.classsync.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.rochiee.classsync.ui.theme.SilverBorder
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
    onNavigateToAuth: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    val spacing = LocalSpacing.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.md)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        item { ScreenSection(title = "Control center", subtitle = "Keep the essentials aligned before you tweak the deeper settings below.") {
            TintedPanel {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    SilverBorder.copy(alpha = 0.18f)
                                )
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "SETTINGS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                AppLogoLockup(
                    subtitle = "Control sync, reminders, and the local study experience from one place",
                    compact = true
                )
                Text(
                    text = "Tune the system once, then let the app stay out of your way.",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Adjust what stays connected, what gets refreshed, and how ClassSync reminds you before urgent work slips.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val useSingleColumn = maxWidth < 420.dp
                    if (useSingleColumn) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            SettingsHeroStatCard(
                                title = "Account",
                                value = if (authState.isSignedIn) "Connected" else "Needs setup",
                                supportingText = authState.userEmail ?: authState.displayName ?: "Google not connected yet",
                                modifier = Modifier.fillMaxWidth(),
                                accent = SkyBlue
                            )
                            SettingsHeroStatCard(
                                title = "Last sync",
                                value = settingsState.lastSyncTimeMillis.formatDateTime(),
                                supportingText = if (syncState.isSyncing) "A sync is currently running" else "Latest successful local refresh",
                                modifier = Modifier.fillMaxWidth(),
                                accent = Sun
                            )
                            SettingsHeroStatCard(
                                title = "Reminder lead",
                                value = "${settingsState.defaultReminderHours}h",
                                supportingText = "Default notice before deadlines",
                                modifier = Modifier.fillMaxWidth(),
                                accent = MintGreen
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            SettingsHeroStatCard(
                                title = "Account",
                                value = if (authState.isSignedIn) "Connected" else "Needs setup",
                                supportingText = authState.userEmail ?: authState.displayName ?: "Google not connected yet",
                                modifier = Modifier.fillMaxWidth(),
                                accent = SkyBlue
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                SettingsHeroStatCard(
                                    title = "Last sync",
                                    value = settingsState.lastSyncTimeMillis.formatDateTime(),
                                    supportingText = if (syncState.isSyncing) "A sync is currently running" else "Latest successful local refresh",
                                    modifier = Modifier.weight(1f),
                                    accent = Sun
                                )
                                SettingsHeroStatCard(
                                    title = "Reminder lead",
                                    value = "${settingsState.defaultReminderHours}h",
                                    supportingText = "Default notice before deadlines",
                                    modifier = Modifier.weight(1f),
                                    accent = MintGreen
                                )
                            }
                        }
                    }
                }
            }
        } }

        item { ScreenSection(title = "Sync hub", subtitle = "Keep live academic data fresh and decide which sources ClassSync should use.") {
            TintedPanel {
                if (authState.isSignedIn) {
                    Text(
                        text = "Connected as ${authState.userEmail ?: authState.displayName ?: "student"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = "Google identity is not connected yet.",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = if (authState.isOAuthConfigured) {
                        "Classroom sync is your primary semester source. Gmail remains optional for catching academic reminders. Automatic background sync refreshes about every 12 hours."
                    } else {
                        "Google sync is disabled until a local OAuth client ID is configured on this machine."
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
                        text = if (syncState.isSyncing) "Syncing..." else "Refresh classroom",
                        onClick = { onSyncEvent(SyncEvent.RunClassroomSync) },
                        modifier = Modifier.widthIn(min = 148.dp),
                        enabled = authState.isSignedIn && settingsState.classroomSyncEnabled && !syncState.isSyncing,
                        showArrow = true
                    )
                    LiquidGlassTextButton(
                        text = if (syncState.isSyncing) "Syncing..." else "Refresh Gmail",
                        onClick = { onSyncEvent(SyncEvent.RunGmailSync) },
                        modifier = Modifier.widthIn(min = 148.dp),
                        enabled = authState.isSignedIn && settingsState.gmailSyncEnabled && !syncState.isSyncing,
                        showArrow = true
                    )
                    LiquidGlassTextButton(
                        text = if (syncState.isSyncing) "Syncing..." else "Run full refresh",
                        onClick = { onSyncEvent(SyncEvent.RunManualFullSync) },
                        modifier = Modifier.widthIn(min = 148.dp),
                        enabled = authState.isSignedIn && !syncState.isSyncing,
                        showArrow = true
                    )
                    if (!authState.isSignedIn) {
                        LiquidGlassTextButton(
                            text = "Connect Google",
                            onClick = onNavigateToAuth,
                            modifier = Modifier.widthIn(min = 148.dp),
                            showArrow = true
                        )
                    }
                }
            }
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
                title = "Background sync",
                description = "Enable WorkManager-based periodic sync jobs that refresh Gmail and Classroom roughly every 12 hours.",
                checked = settingsState.backgroundSyncEnabled,
                onCheckedChange = { onSettingsEvent(SettingsEvent.SetBackgroundSyncEnabled(it)) }
            )
        } }

        item { ScreenSection(title = "Reminders first", subtitle = "Critical due work and ongoing alerts should be easy to control.") {
            TintedPanel {
                Text(text = "Reminder lead time: ${settingsState.defaultReminderHours}h", style = MaterialTheme.typography.titleMedium)
                ResponsiveFlowRow(maxItemsInEachRow = 3) {
                    LiquidGlassTextButton(text = "1 hour", onClick = { onSettingsEvent(SettingsEvent.SetDefaultReminderHours(1)) }, modifier = Modifier.widthIn(min = 92.dp), selected = settingsState.defaultReminderHours == 1)
                    LiquidGlassTextButton(text = "2 hours", onClick = { onSettingsEvent(SettingsEvent.SetDefaultReminderHours(2)) }, modifier = Modifier.widthIn(min = 92.dp), selected = settingsState.defaultReminderHours == 2)
                    LiquidGlassTextButton(text = "6 hours", onClick = { onSettingsEvent(SettingsEvent.SetDefaultReminderHours(6)) }, modifier = Modifier.widthIn(min = 92.dp), selected = settingsState.defaultReminderHours == 6)
                }
                Text(
                    text = "Live due-soon notifications and assignment reminders use this value as the default lead window.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } }

        item { ScreenSection(title = "Appearance", subtitle = "Pick the mode that feels best on your device.") {
            TintedPanel {
                Text(
                    text = "Theme mode",
                    style = MaterialTheme.typography.titleMedium
                )
                ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    ThemeModeButton(
                        label = "Same as system",
                        selected = settingsState.themeMode == ThemeMode.SYSTEM,
                        onClick = { onSettingsEvent(SettingsEvent.SetThemeMode(ThemeMode.SYSTEM)) },
                        modifier = Modifier.widthIn(min = 164.dp)
                    )
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
        } }

        item { ScreenSection(title = "Daily digest", subtitle = "A once-a-day local digest of due work, exams, and course updates.") {
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
                LiquidGlassTextButton(text = "Preview today’s digest", onClick = { onSettingsEvent(SettingsEvent.PreviewDigest) }, modifier = Modifier.fillMaxWidth())
            }
        } }

        item { ScreenSection(title = "Privacy and policy", subtitle = "Review exactly what ClassSync accesses, what stays local, and what you control.") {
            TintedPanel {
                Text(
                    text = "ClassSync stores your task workspace locally on this device. If you sign in with Google, the app may read your Google account email, Google Classroom coursework, announcements, materials, and optional Gmail academic messages only for the sync features you enable.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Classroom sync is read-only. Gmail sync is optional. Exported CSV or JSON files are created only when you trigger an export. Reminder and digest notifications use local app data. ClassSync does not sell your personal data and does not use a custom backend to store your academic content.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "You can disable Gmail sync, disable Classroom sync, sign out of Google, or clear local academic data at any time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LiquidGlassTextButton(
                    text = "Read privacy policy",
                    onClick = onNavigateToPrivacyPolicy,
                    modifier = Modifier.fillMaxWidth(),
                    showArrow = true
                )
            }
        } }

    }
}

@Composable
private fun SettingsHeroStatCard(
    title: String,
    value: String,
    supportingText: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    TintedPanel(
        modifier = modifier.then(Modifier.height(196.dp)),
        accentColor = accent
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(accent.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
