package com.rochiee.classsync.ui.screens.debug

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.auth.AuthEvent
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.event.EventEvent
import com.rochiee.classsync.bloc.event.EventState
import com.rochiee.classsync.bloc.planner.PlannerEvent
import com.rochiee.classsync.bloc.planner.PlannerState
import com.rochiee.classsync.bloc.settings.SettingsEvent
import com.rochiee.classsync.bloc.settings.SettingsState
import com.rochiee.classsync.bloc.sync.SyncEvent
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.bloc.task.TaskEvent
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun DebugScreen(
    authState: AuthUiState,
    taskState: TaskState,
    syncState: SyncState,
    settingsState: SettingsState,
    eventState: EventState,
    plannerState: PlannerState,
    onBeginGoogleSignIn: (Context) -> Intent?,
    onCompleteGoogleSignIn: (Intent?) -> Unit,
    onAuthEvent: (AuthEvent) -> Unit,
    onTaskEvent: (TaskEvent) -> Unit,
    onSyncEvent: (SyncEvent) -> Unit,
    onSettingsEvent: (SettingsEvent) -> Unit,
    onEventEvent: (EventEvent) -> Unit,
    onPlannerEvent: (PlannerEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        onCompleteGoogleSignIn(result.data)
    }
    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        ScreenSection(title = "Debug tools", subtitle = "Existing backend controls moved out of MainActivity.") {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(
                    text = "Sign in",
                    onClick = {
                        onAuthEvent(AuthEvent.ClearAuthError)
                        onBeginGoogleSignIn(context)?.let(signInLauncher::launch)
                    },
                    modifier = Modifier.weight(1f)
                )
                LiquidGlassTextButton(text = "Sign out", onClick = { onAuthEvent(AuthEvent.SignOut) }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(text = "Sync Classroom", onClick = { onSyncEvent(SyncEvent.RunClassroomSync) }, modifier = Modifier.weight(1f))
                LiquidGlassTextButton(text = "Sync Gmail", onClick = { onSyncEvent(SyncEvent.RunGmailSync) }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(text = "Full Sync", onClick = { onSyncEvent(SyncEvent.RunManualFullSync) }, modifier = Modifier.weight(1f))
                LiquidGlassTextButton(text = "Notification Access", onClick = { onTaskEvent(TaskEvent.OpenNotificationAccessSettings) }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(text = "Sample Announcement", onClick = { onEventEvent(EventEvent.AddSampleAnnouncementEvent) }, modifier = Modifier.weight(1f))
                LiquidGlassTextButton(text = "Sample Quiz", onClick = { onEventEvent(EventEvent.AddSampleQuizEvent) }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                LiquidGlassTextButton(text = "Load Today", onClick = { onPlannerEvent(PlannerEvent.LoadToday) }, modifier = Modifier.weight(1f))
                LiquidGlassTextButton(text = "Load Week", onClick = { onPlannerEvent(PlannerEvent.LoadCurrentWeek) }, modifier = Modifier.weight(1f))
            }
            LiquidGlassTextButton(
                text = if (settingsState.gmailSyncEnabled) "Disable Gmail" else "Enable Gmail",
                onClick = { onSettingsEvent(SettingsEvent.SetGmailSyncEnabled(!settingsState.gmailSyncEnabled)) }
            )
        }

        TintedPanel {
            Text(text = "Signed in: ${authState.isSignedIn}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Tasks: ${taskState.tasks.size}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Events: ${eventState.allEvents.size}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Sync logs: ${syncState.logs.size}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Last planner update: ${plannerState.lastUpdatedMillis.formatDateTime()}", style = MaterialTheme.typography.bodyMedium)
        }

        AuthDiagnosticsScreen(authState = authState)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            items(syncState.logs.take(10)) { log ->
                TintedPanel {
                    Text(text = log.source, style = MaterialTheme.typography.titleMedium)
                    Text(text = log.status, style = MaterialTheme.typography.bodyMedium)
                    Text(text = log.message, style = MaterialTheme.typography.bodyMedium)
                    Text(text = log.timestamp.formatDateTime(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
