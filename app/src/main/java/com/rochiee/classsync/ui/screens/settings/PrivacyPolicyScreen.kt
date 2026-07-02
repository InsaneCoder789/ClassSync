package com.rochiee.classsync.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.rochiee.classsync.R
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun PrivacyPolicyScreen() {
    val spacing = LocalSpacing.current
    val uriHandler = LocalUriHandler.current
    val publicPolicyUrl = stringResource(R.string.privacy_policy_public_url)
    val supportEmail = stringResource(R.string.privacy_policy_contact_email)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        item {
            ScreenSection(
                title = "Privacy policy",
                subtitle = "What ClassSync accesses, what stays on-device, and what you control."
            ) {
                TintedPanel {
                    Text(
                        text = "ClassSync is designed as a local-first academic workspace. It connects to Google only for the features you enable, keeps your study data on your device by default, and does not sell your personal data.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (publicPolicyUrl.isNotBlank()) {
                        LiquidGlassTextButton(
                            text = "Open public privacy policy",
                            onClick = { uriHandler.openUri(publicPolicyUrl) },
                            modifier = Modifier.fillMaxWidth(),
                            showArrow = true
                        )
                    }
                }
            }
        }

        item {
            PolicyBlock(
                title = "Data we access",
                body = "If you sign in with Google, ClassSync may access your Google account email, selected Google Classroom coursework, announcements, course materials, and optional Gmail messages that match academic sync rules. The app also stores your manually created tasks, planner state, reminder settings, widget state, and sync preferences locally on your device."
            )
        }

        item {
            PolicyBlock(
                title = "How data is used",
                body = "ClassSync uses this data to build your task list, planner, study views, reminders, widgets, and digest features. Gmail sync is optional and used only to detect academic notifications and reminders. Classroom sync is read-only and used to import class activity into local event and task flows."
            )
        }

        item {
            PolicyBlock(
                title = "Sharing and storage",
                body = "ClassSync does not operate a custom backend for your academic data. Data retrieved from Google APIs is processed locally and stored on-device using app-local storage. Optional task exports that you trigger are written to files on your device. Your sign-in session details are stored locally using encrypted preferences where supported."
            )
        }

        item {
            PolicyBlock(
                title = "Permissions",
                body = "ClassSync requests network access for Google API sync, notification permission for reminders and digest notifications, and Google account authorization for Gmail and Classroom features. If you do not enable a sync source, ClassSync does not use that source."
            )
        }

        item {
            PolicyBlock(
                title = "Your controls",
                body = "You can disconnect Google access by signing out, disable Gmail sync, disable Classroom sync, disable background sync, turn off digest notifications, and delete locally stored academic data from within the app’s account and settings flows."
            )
        }

        item {
            PolicyBlock(
                title = "Retention and contact",
                body = "Locally stored task and event data remains on your device until you delete it, clear app data, or uninstall the app. If you need privacy support or deletion guidance for the published app, contact: $supportEmail"
            )
        }
    }
}

@Composable
private fun PolicyBlock(
    title: String,
    body: String
) {
    TintedPanel {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
