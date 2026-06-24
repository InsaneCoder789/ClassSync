# ClassSync Milestone 14, 17, 20 Output

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:name=".ClassSyncApplication"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Classsync">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Classsync">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".data.notification.ClassSyncNotificationListener"
            android:label="ClassSync Notification Reader"
            android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.service.notification.NotificationListenerService" />
            </intent-filter>
        </service>

        <receiver
            android:name=".reminder.TaskReminderReceiver"
            android:enabled="true"
            android:exported="false" />

        <receiver
            android:name=".widget.ClassSyncWidgetProvider"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="com.rochiee.classsync.widget.ACTION_REFRESH" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/classsync_widget_info" />
        </receiver>
    </application>

</manifest>

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/MainActivity.kt

```kt
package com.rochiee.classsync

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.auth.AuthBlocViewModel
import com.rochiee.classsync.bloc.auth.AuthEvent
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.event.EventBlocViewModel
import com.rochiee.classsync.bloc.event.EventEvent
import com.rochiee.classsync.bloc.event.EventState
import com.rochiee.classsync.bloc.planner.PlannerBlocViewModel
import com.rochiee.classsync.bloc.planner.PlannerEvent
import com.rochiee.classsync.bloc.planner.PlannerState
import com.rochiee.classsync.bloc.settings.SettingsBlocViewModel
import com.rochiee.classsync.bloc.settings.SettingsEvent
import com.rochiee.classsync.bloc.settings.SettingsState
import com.rochiee.classsync.bloc.sync.SyncBlocViewModel
import com.rochiee.classsync.bloc.sync.SyncEvent
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.bloc.task.TaskEvent
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.di.ViewModelFactory
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.ui.theme.ClasssyncTheme
import java.text.DateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    private val taskViewModel: com.rochiee.classsync.bloc.task.TaskBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }

    private val authViewModel: AuthBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }

    private val syncViewModel: SyncBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }

    private val settingsViewModel: SettingsBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }

    private val eventViewModel: EventBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }

    private val plannerViewModel: PlannerBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initial load
        taskViewModel.onEvent(TaskEvent.LoadTasks)
        authViewModel.onEvent(AuthEvent.CheckAuthState)

        setContent {
            ClasssyncTheme {
                val taskState by taskViewModel.state.collectAsState()
                val authState by authViewModel.state.collectAsState()
                val syncState by syncViewModel.state.collectAsState()
                val settingsState by settingsViewModel.state.collectAsState()
                val eventState by eventViewModel.state.collectAsState()
                val plannerState by plannerViewModel.state.collectAsState()
                
                TaskBackendTestScreen(
                    taskState = taskState,
                    onTaskEvent = taskViewModel::onEvent,
                    authState = authState,
                    onAuthEvent = authViewModel::onEvent,
                    syncState = syncState,
                    onSyncEvent = syncViewModel::onEvent,
                    settingsState = settingsState,
                    onSettingsEvent = settingsViewModel::onEvent,
                    eventState = eventState,
                    onEventEvent = eventViewModel::onEvent,
                    plannerState = plannerState,
                    onPlannerEvent = plannerViewModel::onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBackendTestScreen(
    taskState: TaskState,
    onTaskEvent: (TaskEvent) -> Unit,
    authState: AuthUiState,
    onAuthEvent: (AuthEvent) -> Unit,
    syncState: SyncState,
    onSyncEvent: (SyncEvent) -> Unit,
    settingsState: SettingsState,
    onSettingsEvent: (SettingsEvent) -> Unit,
    eventState: EventState,
    onEventEvent: (EventEvent) -> Unit,
    plannerState: PlannerState,
    onPlannerEvent: (PlannerEvent) -> Unit
) {
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val requestNotifications = remember(notificationPermissionLauncher) {
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("ClassSync Backend Test") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Milestone 3: Auth Section
            AuthSection(state = authState, onEvent = onAuthEvent)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Milestone 4 & 5: Sync Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onTaskEvent(TaskEvent.SyncGmailTasks) },
                    modifier = Modifier.weight(1f),
                    enabled = authState.isSignedIn && !taskState.isLoading
                ) {
                    Text("Sync Gmail", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { onTaskEvent(TaskEvent.SyncClassroomTasks) },
                    modifier = Modifier.weight(1f),
                    enabled = authState.isSignedIn && !taskState.isLoading
                ) {
                    Text("Sync Classroom", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onTaskEvent(TaskEvent.ScheduleBackgroundSync) },
                    modifier = Modifier.weight(1f),
                    enabled = authState.isSignedIn
                ) {
                    Text("Schedule Sync", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { onTaskEvent(TaskEvent.CancelBackgroundSync) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel Sync", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onTaskEvent(TaskEvent.RunOneTimeFullSync) },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState.isSignedIn
            ) {
                Text("Run One-Time Full Sync", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            val totalTasks = taskState.tasks.size
            val completedTasks = taskState.tasks.count { it.isCompleted }
            val pendingTasks = totalTasks - completedTasks

            // Milestone 1: Stats Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Total Tasks: $totalTasks", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Pending Tasks: $pendingTasks", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Completed Tasks: $completedTasks", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Milestone 1 & 2: Action Buttons (Simulators & Manual)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onTaskEvent(
                                TaskEvent.AddTaskFromRawText(
                                    rawText = """
                                        New assignment posted: DBMS ER Diagram
                                        Due tomorrow 11:59 PM
                                        Please submit your ER diagram on Google Classroom.
                                        Class: DBMS
                                    """.trimIndent(),
                                    courseName = "DBMS"
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Raw Task", style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = {
                            onTaskEvent(
                                TaskEvent.AddManualTask(
                                    title = "OOPJ Assignment Test",
                                    description = "Complete and upload the Java assignment.",
                                    courseName = "OOPJ",
                                    dueDateMillis = System.currentTimeMillis() + 86400000L
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Manual Task", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onTaskEvent(
                                TaskEvent.SimulateNotificationTask(
                                    packageName = "com.google.android.gm",
                                    title = "New assignment posted: DBMS ER Diagram",
                                    text = "Due tomorrow 11:59 PM. Please submit your ER diagram on Google Classroom."
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sim Gmail Notif", style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = {
                            onTaskEvent(
                                TaskEvent.SimulateNotificationTask(
                                    packageName = "com.google.android.apps.classroom",
                                    title = "Quiz posted: Operating Systems Deadlock",
                                    text = "Due today 11:59 PM. Complete the quiz before deadline."
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sim Class Notif", style = MaterialTheme.typography.labelSmall)
                    }
                }
                
                Button(
                    onClick = {
                        onTaskEvent(TaskEvent.OpenNotificationAccessSettings)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Open Notification Access Settings")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsDebugSection(
                state = settingsState,
                onEvent = onSettingsEvent,
                onTaskEvent = onTaskEvent,
                onRequestReminderPermission = requestNotifications
            )

            Spacer(modifier = Modifier.height(16.dp))

            EventDebugSection(
                state = eventState,
                onEvent = onEventEvent
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlannerDebugSection(
                state = plannerState,
                onEvent = onPlannerEvent
            )

            Spacer(modifier = Modifier.height(16.dp))

            SyncDebugSection(
                state = syncState,
                onEvent = onSyncEvent
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (taskState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (taskState.error != null) {
                Text(text = "Error: ${taskState.error}", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Milestone 1: Task List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(taskState.tasks) { task ->
                    TaskTestItem(
                        task = task,
                        onToggle = { onTaskEvent(TaskEvent.ToggleTaskCompletion(task.id, !task.isCompleted)) },
                        onDelete = { onTaskEvent(TaskEvent.DeleteTask(task)) }
                    )
                }
            }
        }
    }
}

@Composable
fun SyncDebugSection(
    state: SyncState,
    onEvent: (SyncEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Sync Debug", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Last Sync: ${state.lastSyncMillis?.let(::formatTimestamp) ?: "Never"}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (state.isSyncing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (state.errorMessage != null) {
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEvent(SyncEvent.RunManualFullSync) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Manual Full Sync", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { onEvent(SyncEvent.ClearLogs) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear Logs", style = MaterialTheme.typography.labelSmall)
                }
            }

            Text("Recent Sync Logs", style = MaterialTheme.typography.titleSmall)
            state.logs.take(5).forEach { log ->
                SyncLogItem(log = log)
            }
        }
    }
}

@Composable
fun SettingsDebugSection(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onTaskEvent: (TaskEvent) -> Unit,
    onRequestReminderPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Settings Debug", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Stored Last Sync: ${state.lastSyncTimeMillis?.let(::formatTimestamp) ?: "Never"}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (state.errorMessage != null) {
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            SettingsToggleRow(
                label = "Background Sync",
                checked = state.backgroundSyncEnabled,
                onCheckedChange = { onEvent(SettingsEvent.SetBackgroundSyncEnabled(it)) }
            )
            SettingsToggleRow(
                label = "Gmail Sync",
                checked = state.gmailSyncEnabled,
                onCheckedChange = { onEvent(SettingsEvent.SetGmailSyncEnabled(it)) }
            )
            SettingsToggleRow(
                label = "Classroom Sync",
                checked = state.classroomSyncEnabled,
                onCheckedChange = { onEvent(SettingsEvent.SetClassroomSyncEnabled(it)) }
            )
            SettingsToggleRow(
                label = "Notification Parsing",
                checked = state.notificationParsingEnabled,
                onCheckedChange = { onEvent(SettingsEvent.SetNotificationParsingEnabled(it)) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Reminder Hours: ${state.defaultReminderHours}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            onEvent(
                                SettingsEvent.SetDefaultReminderHours(
                                    (state.defaultReminderHours - 1).coerceAtLeast(1)
                                )
                            )
                        }
                    ) {
                        Text("-")
                    }
                    OutlinedButton(
                        onClick = {
                            onEvent(
                                SettingsEvent.SetDefaultReminderHours(
                                    (state.defaultReminderHours + 1).coerceAtMost(24)
                                )
                            )
                        }
                    ) {
                        Text("+")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onTaskEvent(TaskEvent.ExportTasksCsv) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Export CSV", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { onTaskEvent(TaskEvent.ExportTasksJson) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Export JSON", style = MaterialTheme.typography.labelSmall)
                }
            }

            OutlinedButton(
                onClick = onRequestReminderPermission,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Request Reminder Permission", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun EventDebugSection(
    state: EventState,
    onEvent: (EventEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Event Debug", style = MaterialTheme.typography.titleMedium)
            Text("Total Events: ${state.allEvents.size}", style = MaterialTheme.typography.bodyMedium)
            Text("Assignments: ${state.assignments.size}", style = MaterialTheme.typography.bodyMedium)
            Text("Announcements: ${state.announcements.size}", style = MaterialTheme.typography.bodyMedium)
            Text("Materials: ${state.materials.size}", style = MaterialTheme.typography.bodyMedium)
            Text("Quizzes: ${state.quizzes.size}", style = MaterialTheme.typography.bodyMedium)
            Text("Comments: ${state.comments.size}", style = MaterialTheme.typography.bodyMedium)

            if (state.errorMessage != null) {
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEvent(EventEvent.AddSampleAnnouncementEvent) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sample Announcement", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { onEvent(EventEvent.AddSampleMaterialEvent) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sample Material", style = MaterialTheme.typography.labelSmall)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEvent(EventEvent.AddSampleQuizEvent) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sample Quiz", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { onEvent(EventEvent.AddSampleCommentEvent) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sample Comment", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun PlannerDebugSection(
    state: PlannerState,
    onEvent: (PlannerEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Planner Debug", style = MaterialTheme.typography.titleMedium)
            Text("Today Items: ${(state.today?.tasks?.size ?: 0) + (state.today?.events?.size ?: 0)}", style = MaterialTheme.typography.bodyMedium)
            Text("Week Tasks: ${state.currentWeek?.totalTaskCount ?: 0}", style = MaterialTheme.typography.bodyMedium)
            Text("Month Quiz/Exam Count: ${state.currentMonth?.quizExamCount ?: 0}", style = MaterialTheme.typography.bodyMedium)

            if (state.errorMessage != null) {
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEvent(PlannerEvent.LoadToday) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Load Today", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { onEvent(PlannerEvent.LoadCurrentWeek) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Load Week", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { onEvent(PlannerEvent.LoadCurrentMonth) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Load Month", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun AuthSection(
    state: AuthUiState,
    onEvent: (AuthEvent) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Google Auth Foundation", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Status: ${if (state.isSignedIn) "Signed In" else "Signed Out"}")
            if (state.isSignedIn) {
                Text(text = "Email: ${state.userEmail ?: "N/A"}")
                Text(text = "Name: ${state.displayName ?: "N/A"}")
            }
            
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
            
            if (state.errorMessage != null) {
                Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onEvent(AuthEvent.SignIn(context)) },
                    enabled = !state.isSignedIn && !state.isLoading
                ) {
                    Text("Sign In")
                }
                Button(
                    onClick = { onEvent(AuthEvent.SignOut) },
                    enabled = state.isSignedIn && !state.isLoading
                ) {
                    Text("Sign Out")
                }
                Button(onClick = { onEvent(AuthEvent.CheckAuthState) }) {
                    Text("Check")
                }
            }
        }
    }
}

@Composable
fun TaskTestItem(
    task: AcademicTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Title: ${task.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Course: ${task.courseName}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Status: ${if (task.isCompleted) "Completed" else "Pending"}", 
                style = MaterialTheme.typography.bodySmall,
                color = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Text(text = "Source: ${task.source}", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onToggle) {
                    Text(if (task.isCompleted) "Undo" else "Complete")
                }
                OutlinedButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun SyncLogItem(log: SyncLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${log.source} • ${log.status}", style = MaterialTheme.typography.bodyMedium)
            Text(log.message, style = MaterialTheme.typography.bodySmall)
            Text(formatTimestamp(log.timestamp), style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(timestamp))
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/di/AppContainer.kt

```kt
package com.rochiee.classsync.di

import android.content.Context
import androidx.room.Room
import com.rochiee.classsync.auth.AuthTokenProvider
import com.rochiee.classsync.auth.GoogleAuthManager
import com.rochiee.classsync.data.local.database.ClassSyncDatabase
import com.rochiee.classsync.data.local.preferences.SettingsDataStore
import com.rochiee.classsync.data.remote.classroom.ClassroomApiClient
import com.rochiee.classsync.data.remote.classroom.ClassroomRemoteDataSource
import com.rochiee.classsync.data.remote.gmail.GmailApiClient
import com.rochiee.classsync.data.remote.gmail.GmailRemoteDataSource
import com.rochiee.classsync.data.repository.ClassroomEventRepositoryImpl
import com.rochiee.classsync.data.repository.ClassroomRepositoryImpl
import com.rochiee.classsync.data.repository.GmailRepositoryImpl
import com.rochiee.classsync.data.repository.SettingsRepositoryImpl
import com.rochiee.classsync.data.repository.SyncLogRepositoryImpl
import com.rochiee.classsync.data.repository.TaskRepositoryImpl
import com.rochiee.classsync.dashboard.DashboardAggregator
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.GmailRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.planner.GetMonthPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetPlannerRangeUseCase
import com.rochiee.classsync.domain.usecase.planner.GetTodayPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetWeekPlannerUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.event.ConvertEventToTaskUseCase
import com.rochiee.classsync.domain.usecase.event.DeleteClassroomEventUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveAllEventsUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveEventsByTypeUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveRecentEventsUseCase
import com.rochiee.classsync.domain.usecase.event.SaveClassroomEventUseCase
import com.rochiee.classsync.domain.usecase.export.ExportTasksCsvUseCase
import com.rochiee.classsync.domain.usecase.export.ExportTasksJsonUseCase
import com.rochiee.classsync.domain.usecase.gmail.SyncGmailTasksUseCase
import com.rochiee.classsync.domain.usecase.notification.OpenNotificationAccessSettingsUseCase
import com.rochiee.classsync.domain.usecase.settings.ObserveSettingsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetBackgroundSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetClassroomSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDefaultReminderHoursUseCase
import com.rochiee.classsync.domain.usecase.settings.SetGmailSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.domain.usecase.settings.SetNotificationParsingEnabledUseCase
import com.rochiee.classsync.domain.usecase.synclog.AddSyncLogUseCase
import com.rochiee.classsync.domain.usecase.synclog.ClearSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.synclog.ObserveSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.task.AddManualTaskUseCase
import com.rochiee.classsync.domain.usecase.task.DeleteTaskUseCase
import com.rochiee.classsync.domain.usecase.task.MarkTaskCompletedUseCase
import com.rochiee.classsync.domain.usecase.task.ObserveTasksUseCase
import com.rochiee.classsync.domain.usecase.worker.CancelBackgroundSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.RunOneTimeFullSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.ScheduleBackgroundSyncUseCase
import com.rochiee.classsync.domain.usecase.widget.RefreshWidgetsUseCase
import com.rochiee.classsync.export.TaskCsvExporter
import com.rochiee.classsync.export.TaskExportManager
import com.rochiee.classsync.export.TaskJsonExporter
import com.rochiee.classsync.eventengine.ClassroomEventParser
import com.rochiee.classsync.eventengine.EventToTaskConverter
import com.rochiee.classsync.planner.PlannerAggregator
import com.rochiee.classsync.reminder.ReminderScheduler
import com.rochiee.classsync.widget.WidgetDataProvider
import com.rochiee.classsync.widget.WidgetTaskFormatter

interface AppContainer {
    val database: ClassSyncDatabase
    val taskRepository: TaskRepository
    val classroomEventRepository: ClassroomEventRepository
    val settingsRepository: SettingsRepository
    val syncLogRepository: SyncLogRepository
    val classroomEventParser: ClassroomEventParser
    val eventToTaskConverter: EventToTaskConverter
    val dashboardAggregator: DashboardAggregator
    val plannerAggregator: PlannerAggregator
    val widgetDataProvider: WidgetDataProvider
    val widgetTaskFormatter: WidgetTaskFormatter
    val observeTasksUseCase: ObserveTasksUseCase
    val addManualTaskUseCase: AddManualTaskUseCase
    val markTaskCompletedUseCase: MarkTaskCompletedUseCase
    val deleteTaskUseCase: DeleteTaskUseCase
    val openNotificationAccessSettingsUseCase: OpenNotificationAccessSettingsUseCase
    val exportTasksCsvUseCase: ExportTasksCsvUseCase
    val exportTasksJsonUseCase: ExportTasksJsonUseCase
    val observeSettingsUseCase: ObserveSettingsUseCase
    val setBackgroundSyncEnabledUseCase: SetBackgroundSyncEnabledUseCase
    val setGmailSyncEnabledUseCase: SetGmailSyncEnabledUseCase
    val setClassroomSyncEnabledUseCase: SetClassroomSyncEnabledUseCase
    val setNotificationParsingEnabledUseCase: SetNotificationParsingEnabledUseCase
    val setDefaultReminderHoursUseCase: SetDefaultReminderHoursUseCase
    val setLastSyncTimeUseCase: SetLastSyncTimeUseCase
    val observeAllEventsUseCase: ObserveAllEventsUseCase
    val observeEventsByTypeUseCase: ObserveEventsByTypeUseCase
    val observeRecentEventsUseCase: ObserveRecentEventsUseCase
    val saveClassroomEventUseCase: SaveClassroomEventUseCase
    val convertEventToTaskUseCase: ConvertEventToTaskUseCase
    val deleteClassroomEventUseCase: DeleteClassroomEventUseCase
    val getTodayPlannerUseCase: GetTodayPlannerUseCase
    val getWeekPlannerUseCase: GetWeekPlannerUseCase
    val getMonthPlannerUseCase: GetMonthPlannerUseCase
    val getPlannerRangeUseCase: GetPlannerRangeUseCase
    val refreshWidgetsUseCase: RefreshWidgetsUseCase
    val observeSyncLogsUseCase: ObserveSyncLogsUseCase
    val addSyncLogUseCase: AddSyncLogUseCase
    val clearSyncLogsUseCase: ClearSyncLogsUseCase
    val googleAuthManager: GoogleAuthManager
    val gmailRepository: GmailRepository
    val syncGmailTasksUseCase: SyncGmailTasksUseCase
    val classroomRepository: ClassroomRepository
    val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase
    val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase
    val scheduleBackgroundSyncUseCase: ScheduleBackgroundSyncUseCase
    val cancelBackgroundSyncUseCase: CancelBackgroundSyncUseCase
    val runOneTimeFullSyncUseCase: RunOneTimeFullSyncUseCase
}

class AppContainerImpl(private val context: Context) : AppContainer {

    override val database: ClassSyncDatabase by lazy {
        Room.databaseBuilder(
            context,
            ClassSyncDatabase::class.java,
            ClassSyncDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    private val taskCsvExporter: TaskCsvExporter by lazy {
        TaskCsvExporter()
    }

    private val taskJsonExporter: TaskJsonExporter by lazy {
        TaskJsonExporter()
    }

    private val taskExportManager: TaskExportManager by lazy {
        TaskExportManager(
            context = context.applicationContext,
            csvExporter = taskCsvExporter,
            jsonExporter = taskJsonExporter
        )
    }

    override val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database.taskDao, reminderScheduler, context.applicationContext)
    }

    private val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context.applicationContext)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsDataStore)
    }

    override val classroomEventRepository: ClassroomEventRepository by lazy {
        ClassroomEventRepositoryImpl(database.classroomEventDao)
    }

    override val syncLogRepository: SyncLogRepository by lazy {
        SyncLogRepositoryImpl(database.syncLogDao)
    }

    override val classroomEventParser: ClassroomEventParser by lazy {
        ClassroomEventParser()
    }

    override val eventToTaskConverter: EventToTaskConverter by lazy {
        EventToTaskConverter()
    }

    override val dashboardAggregator: DashboardAggregator by lazy {
        DashboardAggregator()
    }

    override val plannerAggregator: PlannerAggregator by lazy {
        PlannerAggregator()
    }

    override val widgetDataProvider: WidgetDataProvider by lazy {
        WidgetDataProvider(taskRepository)
    }

    override val widgetTaskFormatter: WidgetTaskFormatter by lazy {
        WidgetTaskFormatter()
    }

    override val observeTasksUseCase: ObserveTasksUseCase by lazy {
        ObserveTasksUseCase(taskRepository)
    }

    override val addManualTaskUseCase: AddManualTaskUseCase by lazy {
        AddManualTaskUseCase(taskRepository)
    }

    override val markTaskCompletedUseCase: MarkTaskCompletedUseCase by lazy {
        MarkTaskCompletedUseCase(taskRepository)
    }

    override val deleteTaskUseCase: DeleteTaskUseCase by lazy {
        DeleteTaskUseCase(taskRepository)
    }

    override val openNotificationAccessSettingsUseCase: OpenNotificationAccessSettingsUseCase by lazy {
        OpenNotificationAccessSettingsUseCase(context.applicationContext)
    }

    override val exportTasksCsvUseCase: ExportTasksCsvUseCase by lazy {
        ExportTasksCsvUseCase(taskRepository, taskExportManager)
    }

    override val exportTasksJsonUseCase: ExportTasksJsonUseCase by lazy {
        ExportTasksJsonUseCase(taskRepository, taskExportManager)
    }

    override val observeSettingsUseCase: ObserveSettingsUseCase by lazy {
        ObserveSettingsUseCase(settingsRepository)
    }

    override val setBackgroundSyncEnabledUseCase: SetBackgroundSyncEnabledUseCase by lazy {
        SetBackgroundSyncEnabledUseCase(settingsRepository)
    }

    override val setGmailSyncEnabledUseCase: SetGmailSyncEnabledUseCase by lazy {
        SetGmailSyncEnabledUseCase(settingsRepository)
    }

    override val setClassroomSyncEnabledUseCase: SetClassroomSyncEnabledUseCase by lazy {
        SetClassroomSyncEnabledUseCase(settingsRepository)
    }

    override val setNotificationParsingEnabledUseCase: SetNotificationParsingEnabledUseCase by lazy {
        SetNotificationParsingEnabledUseCase(settingsRepository)
    }

    override val setDefaultReminderHoursUseCase: SetDefaultReminderHoursUseCase by lazy {
        SetDefaultReminderHoursUseCase(settingsRepository)
    }

    override val setLastSyncTimeUseCase: SetLastSyncTimeUseCase by lazy {
        SetLastSyncTimeUseCase(settingsRepository)
    }

    override val observeAllEventsUseCase: ObserveAllEventsUseCase by lazy {
        ObserveAllEventsUseCase(classroomEventRepository)
    }

    override val observeEventsByTypeUseCase: ObserveEventsByTypeUseCase by lazy {
        ObserveEventsByTypeUseCase(classroomEventRepository)
    }

    override val observeRecentEventsUseCase: ObserveRecentEventsUseCase by lazy {
        ObserveRecentEventsUseCase(classroomEventRepository)
    }

    override val saveClassroomEventUseCase: SaveClassroomEventUseCase by lazy {
        SaveClassroomEventUseCase(classroomEventRepository)
    }

    override val convertEventToTaskUseCase: ConvertEventToTaskUseCase by lazy {
        ConvertEventToTaskUseCase(classroomEventRepository, taskRepository, eventToTaskConverter)
    }

    override val deleteClassroomEventUseCase: DeleteClassroomEventUseCase by lazy {
        DeleteClassroomEventUseCase(classroomEventRepository)
    }

    override val getTodayPlannerUseCase: GetTodayPlannerUseCase by lazy {
        GetTodayPlannerUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val getWeekPlannerUseCase: GetWeekPlannerUseCase by lazy {
        GetWeekPlannerUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val getMonthPlannerUseCase: GetMonthPlannerUseCase by lazy {
        GetMonthPlannerUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val getPlannerRangeUseCase: GetPlannerRangeUseCase by lazy {
        GetPlannerRangeUseCase(taskRepository, classroomEventRepository, plannerAggregator)
    }

    override val refreshWidgetsUseCase: RefreshWidgetsUseCase by lazy {
        RefreshWidgetsUseCase(context.applicationContext)
    }

    private val reminderScheduler: ReminderScheduler by lazy {
        ReminderScheduler(context.applicationContext, settingsRepository)
    }

    override val observeSyncLogsUseCase: ObserveSyncLogsUseCase by lazy {
        ObserveSyncLogsUseCase(syncLogRepository)
    }

    override val addSyncLogUseCase: AddSyncLogUseCase by lazy {
        AddSyncLogUseCase(syncLogRepository)
    }

    override val clearSyncLogsUseCase: ClearSyncLogsUseCase by lazy {
        ClearSyncLogsUseCase(syncLogRepository)
    }

    override val googleAuthManager: GoogleAuthManager by lazy {
        GoogleAuthManager(context)
    }

    private val authTokenProvider: AuthTokenProvider by lazy {
        AuthTokenProvider(googleAuthManager)
    }

    // Gmail
    private val gmailApiClient: GmailApiClient by lazy {
        GmailApiClient(authTokenProvider)
    }

    private val gmailRemoteDataSource: GmailRemoteDataSource by lazy {
        GmailRemoteDataSource(gmailApiClient)
    }

    override val gmailRepository: GmailRepository by lazy {
        GmailRepositoryImpl(gmailRemoteDataSource)
    }

    override val syncGmailTasksUseCase: SyncGmailTasksUseCase by lazy {
        SyncGmailTasksUseCase(
            gmailRepository,
            taskRepository,
            syncLogRepository,
            classroomEventRepository,
            classroomEventParser,
            eventToTaskConverter,
            settingsRepository,
            setLastSyncTimeUseCase,
            refreshWidgetsUseCase
        )
    }

    // Classroom
    private val classroomApiClient: ClassroomApiClient by lazy {
        ClassroomApiClient(authTokenProvider)
    }

    private val classroomRemoteDataSource: ClassroomRemoteDataSource by lazy {
        ClassroomRemoteDataSource(classroomApiClient)
    }

    override val classroomRepository: ClassroomRepository by lazy {
        ClassroomRepositoryImpl(classroomRemoteDataSource, database.courseDao)
    }

    override val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase by lazy {
        SyncClassroomCoursesUseCase(
            classroomRepository,
            syncLogRepository,
            settingsRepository,
            setLastSyncTimeUseCase
        )
    }

    override val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase by lazy {
        SyncClassroomCourseworkUseCase(
            classroomRepository,
            taskRepository,
            syncLogRepository,
            classroomEventRepository,
            classroomEventParser,
            eventToTaskConverter,
            settingsRepository,
            setLastSyncTimeUseCase,
            refreshWidgetsUseCase
        )
    }

    override val scheduleBackgroundSyncUseCase: ScheduleBackgroundSyncUseCase by lazy {
        ScheduleBackgroundSyncUseCase(context.applicationContext, settingsRepository)
    }

    override val cancelBackgroundSyncUseCase: CancelBackgroundSyncUseCase by lazy {
        CancelBackgroundSyncUseCase(context.applicationContext)
    }

    override val runOneTimeFullSyncUseCase: RunOneTimeFullSyncUseCase by lazy {
        RunOneTimeFullSyncUseCase(context.applicationContext)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/di/ViewModelFactory.kt

```kt
package com.rochiee.classsync.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rochiee.classsync.bloc.auth.AuthBlocViewModel
import com.rochiee.classsync.bloc.event.EventBlocViewModel
import com.rochiee.classsync.bloc.planner.PlannerBlocViewModel
import com.rochiee.classsync.bloc.settings.SettingsBlocViewModel
import com.rochiee.classsync.bloc.sync.SyncBlocViewModel
import com.rochiee.classsync.bloc.task.TaskBlocViewModel

class ViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TaskBlocViewModel::class.java) -> {
                TaskBlocViewModel(
                    container.observeTasksUseCase,
                    container.addManualTaskUseCase,
                    container.markTaskCompletedUseCase,
                    container.deleteTaskUseCase,
                    container.syncGmailTasksUseCase,
                    container.syncClassroomCourseworkUseCase,
                    container.scheduleBackgroundSyncUseCase,
                    container.cancelBackgroundSyncUseCase,
                    container.runOneTimeFullSyncUseCase,
                    container.openNotificationAccessSettingsUseCase,
                    container.addSyncLogUseCase,
                    container.exportTasksCsvUseCase,
                    container.exportTasksJsonUseCase
                ) as T
            }
            modelClass.isAssignableFrom(AuthBlocViewModel::class.java) -> {
                AuthBlocViewModel(
                    container.googleAuthManager
                ) as T
            }
            modelClass.isAssignableFrom(SyncBlocViewModel::class.java) -> {
                SyncBlocViewModel(
                    container.observeSyncLogsUseCase,
                    container.clearSyncLogsUseCase,
                    container.syncGmailTasksUseCase,
                    container.syncClassroomCoursesUseCase,
                    container.syncClassroomCourseworkUseCase
                ) as T
            }
            modelClass.isAssignableFrom(SettingsBlocViewModel::class.java) -> {
                SettingsBlocViewModel(
                    container.observeSettingsUseCase,
                    container.setBackgroundSyncEnabledUseCase,
                    container.setGmailSyncEnabledUseCase,
                    container.setClassroomSyncEnabledUseCase,
                    container.setNotificationParsingEnabledUseCase,
                    container.setDefaultReminderHoursUseCase
                ) as T
            }
            modelClass.isAssignableFrom(EventBlocViewModel::class.java) -> {
                EventBlocViewModel(
                    container.observeAllEventsUseCase,
                    container.observeRecentEventsUseCase,
                    container.saveClassroomEventUseCase,
                    container.convertEventToTaskUseCase,
                    container.deleteClassroomEventUseCase
                ) as T
            }
            modelClass.isAssignableFrom(PlannerBlocViewModel::class.java) -> {
                PlannerBlocViewModel(
                    container.getTodayPlannerUseCase,
                    container.getWeekPlannerUseCase,
                    container.getMonthPlannerUseCase,
                    container.getPlannerRangeUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/database/ClassSyncDatabase.kt

```kt
package com.rochiee.classsync.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rochiee.classsync.data.local.dao.CourseDao
import com.rochiee.classsync.data.local.dao.ClassroomEventDao
import com.rochiee.classsync.data.local.dao.NotificationDao
import com.rochiee.classsync.data.local.dao.SyncLogDao
import com.rochiee.classsync.data.local.dao.TaskDao
import com.rochiee.classsync.data.local.entity.ClassroomEventEntity
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.data.local.entity.NotificationEntity
import com.rochiee.classsync.data.local.entity.SyncLogEntity
import com.rochiee.classsync.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, NotificationEntity::class, CourseEntity::class, SyncLogEntity::class, ClassroomEventEntity::class],
    version = 6,
    exportSchema = false
)
abstract class ClassSyncDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val notificationDao: NotificationDao
    abstract val courseDao: CourseDao
    abstract val syncLogDao: SyncLogDao
    abstract val classroomEventDao: ClassroomEventDao

    companion object {
        const val DATABASE_NAME = "classsync_db"
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/dao/ClassroomEventDao.kt

```kt
package com.rochiee.classsync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rochiee.classsync.data.local.entity.ClassroomEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassroomEventDao {
    @Query("SELECT * FROM classroom_events ORDER BY eventTimeMillis DESC")
    suspend fun getAllEventsSnapshot(): List<ClassroomEventEntity>

    @Query("SELECT * FROM classroom_events ORDER BY eventTimeMillis DESC")
    fun observeAllEvents(): Flow<List<ClassroomEventEntity>>

    @Query("SELECT * FROM classroom_events WHERE eventType = :eventType ORDER BY eventTimeMillis DESC")
    fun observeEventsByType(eventType: String): Flow<List<ClassroomEventEntity>>

    @Query("SELECT * FROM classroom_events ORDER BY eventTimeMillis DESC LIMIT :limit")
    fun observeRecentEvents(limit: Int): Flow<List<ClassroomEventEntity>>

    @Query("""
        SELECT * FROM classroom_events
        WHERE convertedToTask = 0
        AND actionType IN ('TASK_REQUIRED', 'OPTIONAL_READING', 'DEADLINE_UPDATE')
        ORDER BY eventTimeMillis DESC
    """)
    fun observeUnconvertedActionableEvents(): Flow<List<ClassroomEventEntity>>

    @Query("SELECT * FROM classroom_events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): ClassroomEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvent(event: ClassroomEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvents(events: List<ClassroomEventEntity>)

    @Query("UPDATE classroom_events SET convertedToTask = :converted, updatedAtMillis = :updatedAtMillis WHERE id = :eventId")
    suspend fun markConvertedToTask(eventId: String, converted: Boolean, updatedAtMillis: Long)

    @Query("DELETE FROM classroom_events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("DELETE FROM classroom_events")
    suspend fun clearEvents()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/entity/ClassroomEventEntity.kt

```kt
package com.rochiee.classsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classroom_events")
data class ClassroomEventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val courseId: String?,
    val courseName: String?,
    val eventType: String,
    val actionType: String,
    val source: String,
    val sourceId: String?,
    val eventTimeMillis: Long,
    val dueDateMillis: Long?,
    val priority: String,
    val originalText: String?,
    val originalLink: String?,
    val convertedToTask: Boolean,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/mapper/ClassroomEventMapper.kt

```kt
package com.rochiee.classsync.data.local.mapper

import com.rochiee.classsync.data.local.entity.ClassroomEventEntity
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority
import com.rochiee.classsync.domain.model.TaskSource

fun ClassroomEventEntity.toDomain(): ClassroomEvent {
    return ClassroomEvent(
        id = id,
        title = title,
        description = description,
        courseId = courseId,
        courseName = courseName,
        eventType = ClassroomEventType.valueOf(eventType),
        actionType = ClassroomEventActionType.valueOf(actionType),
        source = TaskSource.valueOf(source),
        sourceId = sourceId,
        eventTimeMillis = eventTimeMillis,
        dueDateMillis = dueDateMillis,
        priority = TaskPriority.valueOf(priority),
        originalText = originalText,
        originalLink = originalLink,
        convertedToTask = convertedToTask,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

fun ClassroomEvent.toEntity(): ClassroomEventEntity {
    return ClassroomEventEntity(
        id = id,
        title = title,
        description = description,
        courseId = courseId,
        courseName = courseName,
        eventType = eventType.name,
        actionType = actionType.name,
        source = source.name,
        sourceId = sourceId,
        eventTimeMillis = eventTimeMillis,
        dueDateMillis = dueDateMillis,
        priority = priority.name,
        originalText = originalText,
        originalLink = originalLink,
        convertedToTask = convertedToTask,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/repository/ClassroomEventRepositoryImpl.kt

```kt
package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.dao.ClassroomEventDao
import com.rochiee.classsync.data.local.mapper.toDomain
import com.rochiee.classsync.data.local.mapper.toEntity
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClassroomEventRepositoryImpl(
    private val dao: ClassroomEventDao
) : ClassroomEventRepository {
    override suspend fun getEventsSnapshot(): List<ClassroomEvent> {
        return dao.getAllEventsSnapshot().map { it.toDomain() }
    }

    override fun observeAllEvents(): Flow<List<ClassroomEvent>> {
        return dao.observeAllEvents().map { items -> items.map { it.toDomain() } }
    }

    override fun observeEventsByType(type: ClassroomEventType): Flow<List<ClassroomEvent>> {
        return dao.observeEventsByType(type.name).map { items -> items.map { it.toDomain() } }
    }

    override fun observeRecentEvents(limit: Int): Flow<List<ClassroomEvent>> {
        return dao.observeRecentEvents(limit).map { items -> items.map { it.toDomain() } }
    }

    override fun observeUnconvertedActionableEvents(): Flow<List<ClassroomEvent>> {
        return dao.observeUnconvertedActionableEvents().map { items -> items.map { it.toDomain() } }
    }

    override suspend fun getEventById(eventId: String): ClassroomEvent? {
        return dao.getEventById(eventId)?.toDomain()
    }

    override suspend fun saveEvent(event: ClassroomEvent) {
        dao.upsertEvent(event.toEntity())
    }

    override suspend fun saveEvents(events: List<ClassroomEvent>) {
        if (events.isNotEmpty()) {
            dao.upsertEvents(events.map { it.toEntity() })
        }
    }

    override suspend fun markConvertedToTask(eventId: String, converted: Boolean) {
        dao.markConvertedToTask(eventId, converted, System.currentTimeMillis())
    }

    override suspend fun deleteEvent(eventId: String) {
        dao.deleteEventById(eventId)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/repository/TaskRepositoryImpl.kt

```kt
package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.dao.TaskDao
import com.rochiee.classsync.data.local.mapper.toAcademicTask
import com.rochiee.classsync.data.local.mapper.toTaskEntity
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.reminder.ReminderScheduler
import com.rochiee.classsync.taskengine.DuplicateTaskDetector
import com.rochiee.classsync.widget.ClassSyncWidgetUpdater
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val dao: TaskDao,
    private val reminderScheduler: ReminderScheduler,
    private val appContext: Context
) : TaskRepository {
    override fun observeTasks(): Flow<List<AcademicTask>> {
        return dao.getAllTasks().map { entities ->
            entities.map { it.toAcademicTask() }
        }
    }

    override suspend fun getTasksSnapshot(): List<AcademicTask> {
        return dao.getAllTasksSnapshot().map { it.toAcademicTask() }
    }

    override suspend fun addTask(task: AcademicTask) {
        val existingTasks = dao.getAllTasksSnapshot().map { it.toAcademicTask() }
        val duplicate = DuplicateTaskDetector.findBestDuplicate(existingTasks, task)
        val taskWithTimestamps = task.copy(
            createdAtMillis = task.createdAtMillis.takeIf { it > 0 } ?: System.currentTimeMillis(),
            updatedAtMillis = System.currentTimeMillis()
        )

        if (duplicate != null) {
            val mergedTask = DuplicateTaskDetector.merge(duplicate, taskWithTimestamps)
            dao.updateTask(mergedTask.toTaskEntity())
            reminderScheduler.schedule(mergedTask)
            ClassSyncWidgetUpdater.updateAllWidgets(appContext)
        } else {
            val insertedId = dao.insertTask(taskWithTimestamps.toTaskEntity()).toInt()
            reminderScheduler.schedule(taskWithTimestamps.copy(id = insertedId))
            ClassSyncWidgetUpdater.updateAllWidgets(appContext)
        }
    }

    override suspend fun updateTask(task: AcademicTask) {
        val updatedTask = task.copy(updatedAtMillis = System.currentTimeMillis())
        dao.updateTask(updatedTask.toTaskEntity())
        reminderScheduler.schedule(updatedTask)
        ClassSyncWidgetUpdater.updateAllWidgets(appContext)
    }

    override suspend fun deleteTask(task: AcademicTask) {
        reminderScheduler.cancel(task)
        dao.deleteTask(task.toTaskEntity())
        ClassSyncWidgetUpdater.updateAllWidgets(appContext)
    }

    override suspend fun getTaskById(id: Int): AcademicTask? {
        return dao.getTaskById(id)?.toAcademicTask()
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/notification/ClassSyncNotificationListener.kt

```kt
package com.rochiee.classsync.data.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.data.local.entity.NotificationEntity
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.eventengine.RawClassroomEventInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClassSyncNotificationListener : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val packageName = it.packageName
            if (packageName == "com.google.android.gm" || packageName == "com.google.android.apps.classroom") {
                val extras = it.notification.extras
                val title = extras.getString("android.title") ?: ""
                val text = extras.getCharSequence("android.text")?.toString() ?: ""
                val postedAtMillis = it.postTime

                processNotification(packageName, title, text, postedAtMillis)
            }
        }
    }

    private fun processNotification(packageName: String, title: String, text: String, postedAtMillis: Long) {
        val app = application as ClassSyncApplication
        val container = app.container
        val notificationDao = container.database.notificationDao
        val repository = container.taskRepository
        val syncLogRepository = container.syncLogRepository
        val settingsRepository = container.settingsRepository
        val classroomEventRepository = container.classroomEventRepository
        val classroomEventParser = container.classroomEventParser
        val eventToTaskConverter = container.eventToTaskConverter

        serviceScope.launch {
            val settings = settingsRepository.observeSettings().first()
            if (!settings.notificationParsingEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "NOTIFICATION",
                        status = "SKIPPED",
                        message = "Skipped notification parsing because notification parsing is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return@launch
            }
            val entity = NotificationEntity(
                packageName = packageName,
                title = title,
                text = text,
                postedAtMillis = postedAtMillis
            )
            notificationDao.insertNotification(entity)

            val event = classroomEventParser.parse(
                RawClassroomEventInput(
                    title = title,
                    body = text,
                    courseId = null,
                    courseName = if (packageName == "com.google.android.apps.classroom") {
                        title.split(":").firstOrNull()?.trim()
                    } else {
                        null
                    },
                    source = TaskSource.NOTIFICATION,
                    sourceId = "$packageName:$postedAtMillis",
                    sourcePackageName = packageName,
                    originalLink = null,
                    receivedAtMillis = postedAtMillis
                )
            )

            event?.let {
                classroomEventRepository.saveEvent(it)
                val task = eventToTaskConverter.convert(it)
                task?.let { convertedTask ->
                    repository.addTask(convertedTask)
                    classroomEventRepository.markConvertedToTask(it.id, true)
                }
                syncLogRepository.addLog(
                    SyncLog(
                        source = "NOTIFICATION",
                        status = "SUCCESS",
                        message = "Saved event from notification: ${it.title}",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } ?: syncLogRepository.addLog(
                SyncLog(
                    source = "NOTIFICATION",
                    status = "SKIPPED",
                    message = "Notification did not match academic task rules.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/TaskSource.kt

```kt
package com.rochiee.classsync.domain.model

enum class TaskSource {
    GMAIL,
    CLASSROOM,
    NOTIFICATION,
    MANUAL,
    SYSTEM
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/TaskPriority.kt

```kt
package com.rochiee.classsync.domain.model

enum class TaskPriority(val score: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    companion object {
        fun fromScore(score: Int): TaskPriority {
            return entries.maxByOrNull { priority ->
                if (priority.score <= score) priority.score else Int.MIN_VALUE
            } ?: MEDIUM
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/ClassroomEventType.kt

```kt
package com.rochiee.classsync.domain.model

enum class ClassroomEventType {
    ASSIGNMENT,
    COURSEWORK,
    QUIZ,
    EXAM,
    ANNOUNCEMENT,
    MATERIAL,
    REMINDER,
    COMMENT,
    TEACHER_FEEDBACK,
    DUE_DATE_UPDATE,
    SUBMISSION_UPDATE,
    GRADE_UPDATE,
    UNKNOWN
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/ClassroomEventActionType.kt

```kt
package com.rochiee.classsync.domain.model

enum class ClassroomEventActionType {
    TASK_REQUIRED,
    INFORMATION_ONLY,
    OPTIONAL_READING,
    FEEDBACK_ONLY,
    DEADLINE_UPDATE,
    GRADE_INFO,
    UNKNOWN
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/ClassroomEvent.kt

```kt
package com.rochiee.classsync.domain.model

data class ClassroomEvent(
    val id: String,
    val title: String,
    val description: String? = null,
    val courseId: String? = null,
    val courseName: String? = null,
    val eventType: ClassroomEventType,
    val actionType: ClassroomEventActionType,
    val source: TaskSource,
    val sourceId: String? = null,
    val eventTimeMillis: Long,
    val dueDateMillis: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val originalText: String? = null,
    val originalLink: String? = null,
    val convertedToTask: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/repository/ClassroomEventRepository.kt

```kt
package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import kotlinx.coroutines.flow.Flow

interface ClassroomEventRepository {
    suspend fun getEventsSnapshot(): List<ClassroomEvent>
    fun observeAllEvents(): Flow<List<ClassroomEvent>>
    fun observeEventsByType(type: ClassroomEventType): Flow<List<ClassroomEvent>>
    fun observeRecentEvents(limit: Int): Flow<List<ClassroomEvent>>
    fun observeUnconvertedActionableEvents(): Flow<List<ClassroomEvent>>
    suspend fun getEventById(eventId: String): ClassroomEvent?
    suspend fun saveEvent(event: ClassroomEvent)
    suspend fun saveEvents(events: List<ClassroomEvent>)
    suspend fun markConvertedToTask(eventId: String, converted: Boolean)
    suspend fun deleteEvent(eventId: String)
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/gmail/SyncGmailTasksUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.gmail

import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.GmailRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.domain.usecase.widget.RefreshWidgetsUseCase
import com.rochiee.classsync.eventengine.ClassroomEventParser
import com.rochiee.classsync.eventengine.EventToTaskConverter
import com.rochiee.classsync.eventengine.RawClassroomEventInput
import kotlinx.coroutines.flow.first

class SyncGmailTasksUseCase(
    private val gmailRepository: GmailRepository,
    private val taskRepository: TaskRepository,
    private val syncLogRepository: SyncLogRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val classroomEventParser: ClassroomEventParser,
    private val eventToTaskConverter: EventToTaskConverter,
    private val settingsRepository: SettingsRepository,
    private val setLastSyncTimeUseCase: SetLastSyncTimeUseCase,
    private val refreshWidgetsUseCase: RefreshWidgetsUseCase
) {
    suspend operator fun invoke() {
        try {
            if (!settingsRepository.observeSettings().first().gmailSyncEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "GMAIL",
                        status = "SKIPPED",
                        message = "Skipped Gmail sync because Gmail sync is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return
            }
            val messages = gmailRepository.fetchRecentAcademicMessages()
            var importedCount = 0
            var eventCount = 0
            messages.forEach { message ->
                val event = classroomEventParser.parse(
                    RawClassroomEventInput(
                        title = message.subject,
                        body = listOfNotNull(message.snippet, message.body).joinToString("\n"),
                        courseId = null,
                        courseName = if (message.from?.contains("classroom.google.com") == true) {
                            message.subject?.split(":")?.firstOrNull()?.trim()
                        } else {
                            null
                        },
                        source = TaskSource.GMAIL,
                        sourceId = message.threadId.ifBlank { message.id },
                        sourcePackageName = null,
                        originalLink = message.link,
                        receivedAtMillis = message.internalDateMillis
                    )
                )

                event?.let {
                    classroomEventRepository.saveEvent(it)
                    eventCount += 1
                    val task = eventToTaskConverter.convert(it)
                    if (task != null) {
                        taskRepository.addTask(task)
                        classroomEventRepository.markConvertedToTask(it.id, true)
                        importedCount += 1
                    }
                }
            }

            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL",
                    status = "SUCCESS",
                    message = "Saved $eventCount events and imported $importedCount tasks from ${messages.size} Gmail messages.",
                    timestamp = System.currentTimeMillis()
                )
            )
            setLastSyncTimeUseCase(System.currentTimeMillis())
            refreshWidgetsUseCase()
        } catch (error: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL",
                    status = "ERROR",
                    message = error.message ?: "Gmail sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            throw error
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/classroom/SyncClassroomCourseworkUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.classroom

import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.domain.usecase.widget.RefreshWidgetsUseCase
import com.rochiee.classsync.eventengine.ClassroomEventParser
import com.rochiee.classsync.eventengine.EventToTaskConverter
import com.rochiee.classsync.eventengine.RawClassroomEventInput
import kotlinx.coroutines.flow.first

class SyncClassroomCourseworkUseCase(
    private val classroomRepository: ClassroomRepository,
    private val taskRepository: TaskRepository,
    private val syncLogRepository: SyncLogRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val classroomEventParser: ClassroomEventParser,
    private val eventToTaskConverter: EventToTaskConverter,
    private val settingsRepository: SettingsRepository,
    private val setLastSyncTimeUseCase: SetLastSyncTimeUseCase,
    private val refreshWidgetsUseCase: RefreshWidgetsUseCase
) {
    suspend operator fun invoke() {
        try {
            if (!settingsRepository.observeSettings().first().classroomSyncEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "CLASSROOM_TASKS",
                        status = "SKIPPED",
                        message = "Skipped Classroom coursework sync because Classroom sync is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return
            }
            val remoteCourses = classroomRepository.fetchRemoteCourses()
            var importedCount = 0
            var eventCount = 0

            remoteCourses.forEach { courseDto ->
                val courseWorkList = classroomRepository.fetchRemoteCourseWork(courseDto.courseId)
                courseWorkList.forEach { workDto ->
                    val event = classroomEventParser.parse(
                        RawClassroomEventInput(
                            title = workDto.title,
                            body = workDto.description,
                            courseId = workDto.courseId,
                            courseName = courseDto.name,
                            source = TaskSource.CLASSROOM,
                            sourceId = "${workDto.courseId}:${workDto.id}",
                            sourcePackageName = null,
                            originalLink = workDto.alternateLink,
                            receivedAtMillis = workDto.updateTimeMillis.takeIf { it > 0 } ?: workDto.creationTimeMillis
                        )
                    )
                    event?.let {
                        classroomEventRepository.saveEvent(it)
                        eventCount += 1
                        val task = eventToTaskConverter.convert(it)
                        if (task != null) {
                            taskRepository.addTask(task)
                            classroomEventRepository.markConvertedToTask(it.id, true)
                            importedCount += 1
                        }
                    }
                }
            }

            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_TASKS",
                    status = "SUCCESS",
                    message = "Saved $eventCount Classroom events and imported $importedCount tasks from ${remoteCourses.size} courses.",
                    timestamp = System.currentTimeMillis()
                )
            )
            setLastSyncTimeUseCase(System.currentTimeMillis())
            refreshWidgetsUseCase()
        } catch (error: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_TASKS",
                    status = "ERROR",
                    message = error.message ?: "Classroom coursework sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            throw error
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/event/ObserveAllEventsUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class ObserveAllEventsUseCase(
    private val repository: ClassroomEventRepository
) {
    operator fun invoke() = repository.observeAllEvents()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/event/ObserveEventsByTypeUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class ObserveEventsByTypeUseCase(
    private val repository: ClassroomEventRepository
) {
    operator fun invoke(type: ClassroomEventType) = repository.observeEventsByType(type)
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/event/ObserveRecentEventsUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class ObserveRecentEventsUseCase(
    private val repository: ClassroomEventRepository
) {
    operator fun invoke(limit: Int = 20) = repository.observeRecentEvents(limit)
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/event/SaveClassroomEventUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class SaveClassroomEventUseCase(
    private val repository: ClassroomEventRepository
) {
    suspend operator fun invoke(event: ClassroomEvent) {
        repository.saveEvent(event)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/event/ConvertEventToTaskUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.eventengine.EventToTaskConverter

class ConvertEventToTaskUseCase(
    private val classroomEventRepository: ClassroomEventRepository,
    private val taskRepository: TaskRepository,
    private val eventToTaskConverter: EventToTaskConverter
) {
    suspend operator fun invoke(eventId: String): ClassroomEvent? {
        val event = classroomEventRepository.getEventById(eventId) ?: return null
        val task = eventToTaskConverter.convert(event)
        if (task != null) {
            taskRepository.addTask(task)
            classroomEventRepository.markConvertedToTask(eventId, true)
        }
        return event
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/event/DeleteClassroomEventUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.event

import com.rochiee.classsync.domain.repository.ClassroomEventRepository

class DeleteClassroomEventUseCase(
    private val repository: ClassroomEventRepository
) {
    suspend operator fun invoke(eventId: String) {
        repository.deleteEvent(eventId)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/planner/GetTodayPlannerUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.planner

import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.planner.PlannerAggregator
import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.planner.PlannerFilter

class GetTodayPlannerUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val plannerAggregator: PlannerAggregator
) {
    suspend operator fun invoke(filter: PlannerFilter = PlannerFilter()): PlannerDay {
        return plannerAggregator.buildTodayPlanner(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot(),
            filter = filter
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/planner/GetWeekPlannerUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.planner

import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.planner.PlannerAggregator
import com.rochiee.classsync.planner.PlannerFilter
import com.rochiee.classsync.planner.PlannerWeek

class GetWeekPlannerUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val plannerAggregator: PlannerAggregator
) {
    suspend operator fun invoke(filter: PlannerFilter = PlannerFilter()): PlannerWeek {
        return plannerAggregator.buildWeekPlanner(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot(),
            filter = filter
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/planner/GetMonthPlannerUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.planner

import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.planner.PlannerAggregator
import com.rochiee.classsync.planner.PlannerFilter
import com.rochiee.classsync.planner.PlannerMonth

class GetMonthPlannerUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val plannerAggregator: PlannerAggregator
) {
    suspend operator fun invoke(filter: PlannerFilter = PlannerFilter()): PlannerMonth {
        return plannerAggregator.buildMonthPlanner(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot(),
            filter = filter
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/planner/GetPlannerRangeUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.planner

import com.rochiee.classsync.domain.repository.ClassroomEventRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.planner.PlannerAggregator
import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.planner.PlannerFilter

class GetPlannerRangeUseCase(
    private val taskRepository: TaskRepository,
    private val classroomEventRepository: ClassroomEventRepository,
    private val plannerAggregator: PlannerAggregator
) {
    suspend operator fun invoke(
        startMillis: Long,
        endMillis: Long,
        filter: PlannerFilter = PlannerFilter()
    ): List<PlannerDay> {
        return plannerAggregator.buildRangePlanner(
            tasks = taskRepository.getTasksSnapshot(),
            events = classroomEventRepository.getEventsSnapshot(),
            startMillis = startMillis,
            endMillis = endMillis,
            filter = filter
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/widget/RefreshWidgetsUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.widget

import android.content.Context
import com.rochiee.classsync.widget.ClassSyncWidgetUpdater

class RefreshWidgetsUseCase(
    private val context: Context
) {
    operator fun invoke() {
        ClassSyncWidgetUpdater.updateAllWidgets(context)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/eventengine/RawClassroomEventInput.kt

```kt
package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.TaskSource

data class RawClassroomEventInput(
    val title: String?,
    val body: String?,
    val courseId: String?,
    val courseName: String?,
    val source: TaskSource,
    val sourceId: String?,
    val sourcePackageName: String?,
    val originalLink: String?,
    val receivedAtMillis: Long
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/eventengine/ClassroomEventFingerprintGenerator.kt

```kt
package com.rochiee.classsync.eventengine

import java.security.MessageDigest

object ClassroomEventFingerprintGenerator {
    fun generate(input: RawClassroomEventInput, normalizedText: String): String {
        val raw = listOf(
            input.source.name,
            input.sourceId.orEmpty(),
            input.courseId.orEmpty(),
            input.courseName.orEmpty(),
            input.title.orEmpty(),
            normalizedText
        ).joinToString("|")

        return MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(24)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/eventengine/ClassroomEventClassifier.kt

```kt
package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType

object ClassroomEventClassifier {
    private val assignmentKeywords = listOf("assignment", "assigned", "submit", "upload", "turn in", "homework")
    private val courseworkKeywords = listOf("coursework", "classwork", "work posted")
    private val quizKeywords = listOf("quiz", "mcq", "test quiz")
    private val examKeywords = listOf("exam", "midsem", "endsem", "final", "viva", "practical")
    private val announcementKeywords = listOf("announcement", "announced", "notice", "informed", "tomorrow's class", "class cancelled", "class rescheduled", "online class")
    private val materialKeywords = listOf("material", "notes", "pdf", "slides", "chapter", "reading", "resource", "posted material")
    private val reminderKeywords = listOf("reminder", "due soon", "due today", "due tomorrow", "deadline")
    private val commentKeywords = listOf("commented", "private comment", "class comment")
    private val feedbackKeywords = listOf("feedback", "reviewed", "returned", "suggestion")
    private val dueDateUpdateKeywords = listOf("due date changed", "deadline changed", "extended", "extension")
    private val submissionUpdateKeywords = listOf("submitted", "turned in", "returned", "missing")
    private val gradeUpdateKeywords = listOf("grade posted", "marks", "scored", "graded")

    fun classify(text: String): ClassroomEventType {
        val normalized = text.lowercase()
        return when {
            containsAny(normalized, dueDateUpdateKeywords) -> ClassroomEventType.DUE_DATE_UPDATE
            containsAny(normalized, gradeUpdateKeywords) -> ClassroomEventType.GRADE_UPDATE
            containsAny(normalized, feedbackKeywords) -> ClassroomEventType.TEACHER_FEEDBACK
            containsAny(normalized, commentKeywords) -> ClassroomEventType.COMMENT
            containsAny(normalized, reminderKeywords) -> ClassroomEventType.REMINDER
            containsAny(normalized, examKeywords) -> ClassroomEventType.EXAM
            containsAny(normalized, quizKeywords) -> ClassroomEventType.QUIZ
            containsAny(normalized, assignmentKeywords) -> ClassroomEventType.ASSIGNMENT
            containsAny(normalized, courseworkKeywords) -> ClassroomEventType.COURSEWORK
            containsAny(normalized, announcementKeywords) -> ClassroomEventType.ANNOUNCEMENT
            containsAny(normalized, materialKeywords) -> ClassroomEventType.MATERIAL
            containsAny(normalized, submissionUpdateKeywords) -> ClassroomEventType.SUBMISSION_UPDATE
            else -> ClassroomEventType.UNKNOWN
        }
    }

    fun actionTypeFor(eventType: ClassroomEventType): ClassroomEventActionType {
        return when (eventType) {
            ClassroomEventType.ASSIGNMENT,
            ClassroomEventType.COURSEWORK,
            ClassroomEventType.QUIZ,
            ClassroomEventType.EXAM,
            ClassroomEventType.REMINDER -> ClassroomEventActionType.TASK_REQUIRED
            ClassroomEventType.MATERIAL -> ClassroomEventActionType.OPTIONAL_READING
            ClassroomEventType.ANNOUNCEMENT,
            ClassroomEventType.SUBMISSION_UPDATE -> ClassroomEventActionType.INFORMATION_ONLY
            ClassroomEventType.COMMENT,
            ClassroomEventType.TEACHER_FEEDBACK -> ClassroomEventActionType.FEEDBACK_ONLY
            ClassroomEventType.DUE_DATE_UPDATE -> ClassroomEventActionType.DEADLINE_UPDATE
            ClassroomEventType.GRADE_UPDATE -> ClassroomEventActionType.GRADE_INFO
            ClassroomEventType.UNKNOWN -> ClassroomEventActionType.UNKNOWN
        }
    }

    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { text.contains(it) }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/eventengine/EventPriorityEngine.kt

```kt
package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority

object EventPriorityEngine {
    fun priorityFor(
        eventType: ClassroomEventType,
        actionType: ClassroomEventActionType,
        dueDateMillis: Long?,
        nowMillis: Long = System.currentTimeMillis()
    ): TaskPriority {
        if (dueDateMillis != null) {
            val hoursUntilDue = (dueDateMillis - nowMillis) / (60L * 60L * 1000L)
            if (hoursUntilDue <= 12) return TaskPriority.URGENT
            if (hoursUntilDue <= 48) return TaskPriority.HIGH
        }

        return when {
            eventType == ClassroomEventType.EXAM || eventType == ClassroomEventType.QUIZ -> TaskPriority.HIGH
            actionType == ClassroomEventActionType.TASK_REQUIRED || actionType == ClassroomEventActionType.DEADLINE_UPDATE -> TaskPriority.HIGH
            actionType == ClassroomEventActionType.OPTIONAL_READING -> TaskPriority.MEDIUM
            else -> TaskPriority.LOW
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/eventengine/ClassroomEventParser.kt

```kt
package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.taskengine.DeadlineParser

class ClassroomEventParser {
    fun parse(input: RawClassroomEventInput): ClassroomEvent? {
        val title = input.title?.trim().orEmpty()
        val body = input.body?.trim().orEmpty()
        val originalText = listOf(title, body).filter { it.isNotBlank() }.joinToString("\n").trim()
        if (originalText.isBlank()) return null

        val normalizedText = cleanText(originalText)
        val eventType = ClassroomEventClassifier.classify(normalizedText)
        val actionType = ClassroomEventClassifier.actionTypeFor(eventType)
        val dueDate = DeadlineParser.parse(originalText, input.receivedAtMillis)
        val eventId = ClassroomEventFingerprintGenerator.generate(input, normalizedText)
        val priority = EventPriorityEngine.priorityFor(eventType, actionType, dueDate, input.receivedAtMillis)

        return ClassroomEvent(
            id = eventId,
            title = if (title.isNotBlank()) title else normalizedText.take(80),
            description = body.ifBlank { null },
            courseId = input.courseId,
            courseName = input.courseName,
            eventType = eventType,
            actionType = actionType,
            source = input.source,
            sourceId = input.sourceId,
            eventTimeMillis = input.receivedAtMillis,
            dueDateMillis = dueDate,
            priority = priority,
            originalText = originalText,
            originalLink = input.originalLink,
            convertedToTask = false,
            createdAtMillis = input.receivedAtMillis,
            updatedAtMillis = input.receivedAtMillis
        )
    }

    private fun cleanText(text: String): String {
        return text.replace(Regex("\\s+"), " ").trim()
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/eventengine/EventToTaskConverter.kt

```kt
package com.rochiee.classsync.eventengine

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType

class EventToTaskConverter {
    fun convert(event: ClassroomEvent): AcademicTask? {
        if (!isActionable(event)) return null

        return AcademicTask(
            title = event.title,
            description = event.description ?: event.originalText.orEmpty(),
            courseName = event.courseName ?: "Unknown Course",
            isCompleted = false,
            dueDate = event.dueDateMillis,
            priority = event.priority.score,
            source = event.source.name.lowercase().replaceFirstChar { it.uppercase() },
            sourceId = event.id,
            sourceLink = event.originalLink,
            createdAtMillis = event.createdAtMillis,
            updatedAtMillis = event.updatedAtMillis
        )
    }

    private fun isActionable(event: ClassroomEvent): Boolean {
        return when (event.eventType) {
            ClassroomEventType.ASSIGNMENT,
            ClassroomEventType.COURSEWORK,
            ClassroomEventType.QUIZ,
            ClassroomEventType.EXAM,
            ClassroomEventType.REMINDER -> true
            ClassroomEventType.MATERIAL -> {
                val text = listOf(event.title, event.description.orEmpty(), event.originalText.orEmpty())
                    .joinToString(" ")
                    .lowercase()
                listOf("read", "complete", "prepare", "revise", "before next class").any { text.contains(it) }
            }
            else -> false
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/dashboard/DashboardSummary.kt

```kt
package com.rochiee.classsync.dashboard

data class DashboardSummary(
    val todayTaskCount: Int,
    val upcomingTaskCount: Int,
    val overdueTaskCount: Int,
    val announcementCount: Int,
    val materialCount: Int,
    val quizCount: Int,
    val examCount: Int,
    val recentEventCount: Int,
    val lastSyncMillis: Long?
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/dashboard/DashboardAggregator.kt

```kt
package com.rochiee.classsync.dashboard

import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.SyncLog
import java.util.Calendar

class DashboardAggregator {
    fun buildSummary(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        syncLogs: List<SyncLog>
    ): DashboardSummary {
        val todayWindow = todayWindow()
        val now = System.currentTimeMillis()

        return DashboardSummary(
            todayTaskCount = tasks.count { task -> task.dueDate?.let { it in todayWindow.first..todayWindow.second } == true },
            upcomingTaskCount = tasks.count { task -> task.dueDate?.let { it >= now } == true && !task.isCompleted },
            overdueTaskCount = tasks.count { task -> task.dueDate?.let { it < now } == true && !task.isCompleted },
            announcementCount = events.count { it.eventType == ClassroomEventType.ANNOUNCEMENT },
            materialCount = events.count { it.eventType == ClassroomEventType.MATERIAL },
            quizCount = events.count { it.eventType == ClassroomEventType.QUIZ },
            examCount = events.count { it.eventType == ClassroomEventType.EXAM },
            recentEventCount = events.size,
            lastSyncMillis = syncLogs.maxOfOrNull { it.timestamp }
        )
    }

    private fun todayWindow(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        return start to calendar.timeInMillis
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/event/EventEvent.kt

```kt
package com.rochiee.classsync.bloc.event

import com.rochiee.classsync.domain.model.ClassroomEventType

sealed class EventEvent {
    object LoadEvents : EventEvent()
    data class LoadEventsByType(val type: ClassroomEventType) : EventEvent()
    object LoadRecentEvents : EventEvent()
    data class DeleteEvent(val eventId: String) : EventEvent()
    data class ConvertEventToTask(val eventId: String) : EventEvent()
    object AddSampleAnnouncementEvent : EventEvent()
    object AddSampleMaterialEvent : EventEvent()
    object AddSampleQuizEvent : EventEvent()
    object AddSampleCommentEvent : EventEvent()
    object ClearError : EventEvent()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/event/EventState.kt

```kt
package com.rochiee.classsync.bloc.event

import com.rochiee.classsync.domain.model.ClassroomEvent

data class EventState(
    val isLoading: Boolean = false,
    val allEvents: List<ClassroomEvent> = emptyList(),
    val assignments: List<ClassroomEvent> = emptyList(),
    val coursework: List<ClassroomEvent> = emptyList(),
    val quizzes: List<ClassroomEvent> = emptyList(),
    val exams: List<ClassroomEvent> = emptyList(),
    val announcements: List<ClassroomEvent> = emptyList(),
    val materials: List<ClassroomEvent> = emptyList(),
    val reminders: List<ClassroomEvent> = emptyList(),
    val comments: List<ClassroomEvent> = emptyList(),
    val feedback: List<ClassroomEvent> = emptyList(),
    val gradeUpdates: List<ClassroomEvent> = emptyList(),
    val recentEvents: List<ClassroomEvent> = emptyList(),
    val errorMessage: String? = null,
    val lastUpdatedMillis: Long? = null
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/event/EventBlocViewModel.kt

```kt
package com.rochiee.classsync.bloc.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventActionType
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority
import com.rochiee.classsync.domain.model.TaskSource
import com.rochiee.classsync.domain.usecase.event.ConvertEventToTaskUseCase
import com.rochiee.classsync.domain.usecase.event.DeleteClassroomEventUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveAllEventsUseCase
import com.rochiee.classsync.domain.usecase.event.ObserveRecentEventsUseCase
import com.rochiee.classsync.domain.usecase.event.SaveClassroomEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventBlocViewModel(
    private val observeAllEventsUseCase: ObserveAllEventsUseCase,
    private val observeRecentEventsUseCase: ObserveRecentEventsUseCase,
    private val saveClassroomEventUseCase: SaveClassroomEventUseCase,
    private val convertEventToTaskUseCase: ConvertEventToTaskUseCase,
    private val deleteClassroomEventUseCase: DeleteClassroomEventUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(EventState(isLoading = true))
    val state: StateFlow<EventState> = _state.asStateFlow()

    init {
        onEvent(EventEvent.LoadEvents)
        onEvent(EventEvent.LoadRecentEvents)
    }

    fun onEvent(event: EventEvent) {
        when (event) {
            EventEvent.LoadEvents -> observeAllEvents()
            is EventEvent.LoadEventsByType -> observeByType(event.type)
            EventEvent.LoadRecentEvents -> observeRecentEvents()
            is EventEvent.DeleteEvent -> deleteEvent(event.eventId)
            is EventEvent.ConvertEventToTask -> convertEvent(event.eventId)
            EventEvent.AddSampleAnnouncementEvent -> saveSampleEvent(sampleAnnouncement())
            EventEvent.AddSampleMaterialEvent -> saveSampleEvent(sampleMaterial())
            EventEvent.AddSampleQuizEvent -> saveSampleEvent(sampleQuiz())
            EventEvent.AddSampleCommentEvent -> saveSampleEvent(sampleComment())
            EventEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeAllEvents() {
        observeAllEventsUseCase()
            .onEach { events -> _state.update { buildCategorizedState(it, events) } }
            .catch { error -> _state.update { it.copy(isLoading = false, errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private fun observeByType(type: ClassroomEventType) {
        observeAllEventsUseCase()
            .onEach { events ->
                val filtered = events.filter { it.eventType == type }
                _state.update { buildCategorizedState(it, filtered) }
            }
            .catch { error -> _state.update { it.copy(errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private fun observeRecentEvents() {
        observeRecentEventsUseCase()
            .onEach { events ->
                _state.update {
                    it.copy(recentEvents = events, lastUpdatedMillis = System.currentTimeMillis())
                }
            }
            .catch { error -> _state.update { it.copy(errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private fun saveSampleEvent(event: ClassroomEvent) {
        viewModelScope.launch {
            try {
                saveClassroomEventUseCase(event)
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun convertEvent(eventId: String) {
        viewModelScope.launch {
            try {
                convertEventToTaskUseCase(eventId)
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                deleteClassroomEventUseCase(eventId)
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    private fun buildCategorizedState(current: EventState, events: List<ClassroomEvent>): EventState {
        return current.copy(
            isLoading = false,
            allEvents = events,
            assignments = events.filter { it.eventType == ClassroomEventType.ASSIGNMENT },
            coursework = events.filter { it.eventType == ClassroomEventType.COURSEWORK },
            quizzes = events.filter { it.eventType == ClassroomEventType.QUIZ },
            exams = events.filter { it.eventType == ClassroomEventType.EXAM },
            announcements = events.filter { it.eventType == ClassroomEventType.ANNOUNCEMENT },
            materials = events.filter { it.eventType == ClassroomEventType.MATERIAL },
            reminders = events.filter { it.eventType == ClassroomEventType.REMINDER },
            comments = events.filter { it.eventType == ClassroomEventType.COMMENT },
            feedback = events.filter { it.eventType == ClassroomEventType.TEACHER_FEEDBACK },
            gradeUpdates = events.filter { it.eventType == ClassroomEventType.GRADE_UPDATE },
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    private fun sampleAnnouncement(): ClassroomEvent = ClassroomEvent(
        id = "sample_announcement",
        title = "Announcement: Tomorrow's DBMS class will be online",
        description = "Please join using the Google Meet link at 9 AM.",
        courseName = "DBMS",
        eventType = ClassroomEventType.ANNOUNCEMENT,
        actionType = ClassroomEventActionType.INFORMATION_ONLY,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        priority = TaskPriority.LOW,
        originalText = "Announcement: Tomorrow's DBMS class will be online\nPlease join using the Google Meet link at 9 AM."
    )

    private fun sampleMaterial(): ClassroomEvent = ClassroomEvent(
        id = "sample_material",
        title = "New material posted: OS Deadlock Notes PDF",
        description = "Read Chapter 3 before next class.",
        courseName = "Operating Systems",
        eventType = ClassroomEventType.MATERIAL,
        actionType = ClassroomEventActionType.OPTIONAL_READING,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        dueDateMillis = System.currentTimeMillis() + 86_400_000L,
        priority = TaskPriority.MEDIUM,
        originalText = "New material posted: OS Deadlock Notes PDF\nRead Chapter 3 before next class."
    )

    private fun sampleQuiz(): ClassroomEvent = ClassroomEvent(
        id = "sample_quiz",
        title = "Quiz posted: Operating Systems Deadlock",
        description = "Due today 11:59 PM. Complete before deadline.",
        courseName = "Operating Systems",
        eventType = ClassroomEventType.QUIZ,
        actionType = ClassroomEventActionType.TASK_REQUIRED,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        dueDateMillis = System.currentTimeMillis() + 43_200_000L,
        priority = TaskPriority.HIGH,
        originalText = "Quiz posted: Operating Systems Deadlock\nDue today 11:59 PM. Complete before deadline."
    )

    private fun sampleComment(): ClassroomEvent = ClassroomEvent(
        id = "sample_comment",
        title = "Teacher commented on DBMS Assignment 2",
        description = "Please improve the ER diagram relationship labels.",
        courseName = "DBMS",
        eventType = ClassroomEventType.COMMENT,
        actionType = ClassroomEventActionType.FEEDBACK_ONLY,
        source = TaskSource.MANUAL,
        eventTimeMillis = System.currentTimeMillis(),
        priority = TaskPriority.LOW,
        originalText = "Teacher commented on DBMS Assignment 2\nPlease improve the ER diagram relationship labels."
    )
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/planner/PlannerItemType.kt

```kt
package com.rochiee.classsync.planner

enum class PlannerItemType {
    TASK,
    ASSIGNMENT,
    COURSEWORK,
    QUIZ,
    EXAM,
    ANNOUNCEMENT,
    MATERIAL,
    REMINDER,
    COMMENT,
    GRADE_UPDATE,
    SYNC_EVENT,
    UNKNOWN
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/planner/PlannerItem.kt

```kt
package com.rochiee.classsync.planner

import com.rochiee.classsync.domain.model.TaskPriority

data class PlannerItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val courseId: String? = null,
    val courseName: String? = null,
    val itemType: PlannerItemType,
    val sourceId: String? = null,
    val sourceType: String,
    val dateMillis: Long,
    val dueDateMillis: Long? = null,
    val priority: TaskPriority,
    val isCompleted: Boolean = false,
    val originalLink: String? = null
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/planner/PlannerDay.kt

```kt
package com.rochiee.classsync.planner

data class PlannerDay(
    val dateStartMillis: Long,
    val dateEndMillis: Long,
    val tasks: List<PlannerItem>,
    val events: List<PlannerItem>,
    val dueItems: List<PlannerItem>,
    val highPriorityItems: List<PlannerItem>
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/planner/PlannerWeek.kt

```kt
package com.rochiee.classsync.planner

data class PlannerWeek(
    val weekStartMillis: Long,
    val weekEndMillis: Long,
    val days: List<PlannerDay>,
    val totalTaskCount: Int,
    val completedTaskCount: Int,
    val overdueTaskCount: Int,
    val quizExamCount: Int
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/planner/PlannerMonth.kt

```kt
package com.rochiee.classsync.planner

data class PlannerMonth(
    val monthStartMillis: Long,
    val monthEndMillis: Long,
    val weeks: List<PlannerWeek>,
    val totalTaskCount: Int,
    val completedTaskCount: Int,
    val overdueTaskCount: Int,
    val announcementCount: Int,
    val materialCount: Int,
    val quizExamCount: Int
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/planner/PlannerFilter.kt

```kt
package com.rochiee.classsync.planner

data class PlannerFilter(
    val showTasks: Boolean = true,
    val showAssignments: Boolean = true,
    val showQuizzes: Boolean = true,
    val showExams: Boolean = true,
    val showAnnouncements: Boolean = true,
    val showMaterials: Boolean = true,
    val showCompleted: Boolean = true,
    val courseId: String? = null
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/planner/PlannerAggregator.kt

```kt
package com.rochiee.classsync.planner

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.ClassroomEvent
import com.rochiee.classsync.domain.model.ClassroomEventType
import com.rochiee.classsync.domain.model.TaskPriority
import java.util.Calendar

class PlannerAggregator {
    fun buildTodayPlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): PlannerDay {
        val bounds = dayBounds(nowMillis)
        return buildPlannerDay(tasks, events, bounds.first, bounds.second, filter, nowMillis)
    }

    fun buildWeekPlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): PlannerWeek {
        val calendar = calendarAt(nowMillis)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val weekStart = dayBounds(calendar.timeInMillis).first
        val days = (0 until 7).map { offset ->
            val dayStart = weekStart + offset * DAY_MILLIS
            val dayEnd = dayStart + DAY_MILLIS - 1
            buildPlannerDay(tasks, events, dayStart, dayEnd, filter, nowMillis)
        }
        val weekEnd = days.last().dateEndMillis
        return PlannerWeek(
            weekStartMillis = weekStart,
            weekEndMillis = weekEnd,
            days = days,
            totalTaskCount = days.sumOf { it.tasks.size },
            completedTaskCount = days.sumOf { day -> day.tasks.count { it.isCompleted } },
            overdueTaskCount = days.sumOf { day ->
                day.dueItems.count { item -> !item.isCompleted && item.dueDateMillis?.let { it < nowMillis } == true }
            },
            quizExamCount = days.sumOf { day ->
                day.events.count { it.itemType == PlannerItemType.QUIZ || it.itemType == PlannerItemType.EXAM }
            }
        )
    }

    fun buildMonthPlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): PlannerMonth {
        val calendar = calendarAt(nowMillis)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = dayBounds(calendar.timeInMillis).first
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val monthEnd = calendar.timeInMillis

        val weeks = mutableListOf<PlannerWeek>()
        var cursor = monthStart
        while (cursor <= monthEnd) {
            weeks += buildWeekPlanner(tasks, events, filter, cursor)
            cursor += 7 * DAY_MILLIS
        }

        val monthItems = weeks.flatMap { it.days }.flatMap { it.tasks + it.events }
        return PlannerMonth(
            monthStartMillis = monthStart,
            monthEndMillis = monthEnd,
            weeks = weeks,
            totalTaskCount = weeks.sumOf { it.totalTaskCount },
            completedTaskCount = weeks.sumOf { it.completedTaskCount },
            overdueTaskCount = weeks.sumOf { it.overdueTaskCount },
            announcementCount = monthItems.count { it.itemType == PlannerItemType.ANNOUNCEMENT },
            materialCount = monthItems.count { it.itemType == PlannerItemType.MATERIAL },
            quizExamCount = monthItems.count { it.itemType == PlannerItemType.QUIZ || it.itemType == PlannerItemType.EXAM }
        )
    }

    fun buildRangePlanner(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        startMillis: Long,
        endMillis: Long,
        filter: PlannerFilter = PlannerFilter(),
        nowMillis: Long = System.currentTimeMillis()
    ): List<PlannerDay> {
        val start = dayBounds(startMillis).first
        val end = dayBounds(endMillis).second
        val result = mutableListOf<PlannerDay>()
        var cursor = start
        while (cursor <= end) {
            result += buildPlannerDay(tasks, events, cursor, cursor + DAY_MILLIS - 1, filter, nowMillis)
            cursor += DAY_MILLIS
        }
        return result
    }

    private fun buildPlannerDay(
        tasks: List<AcademicTask>,
        events: List<ClassroomEvent>,
        dayStart: Long,
        dayEnd: Long,
        filter: PlannerFilter,
        nowMillis: Long
    ): PlannerDay {
        val taskItems = tasks.mapNotNull { task -> task.toPlannerItem() }
        val eventItems = events.mapNotNull { event -> event.toPlannerItem() }
        val items = (taskItems + eventItems)
            .filter { item -> item.dateMillis in dayStart..dayEnd || item.dueDateMillis?.let { it in dayStart..dayEnd } == true }
            .filter { item -> includeItem(item, filter) }

        val tasksForDay = items.filter { it.itemType == PlannerItemType.TASK || it.itemType in setOf(PlannerItemType.ASSIGNMENT, PlannerItemType.COURSEWORK, PlannerItemType.QUIZ, PlannerItemType.EXAM) }
        val eventsForDay = items - tasksForDay.toSet()
        val dueItems = items.filter { it.dueDateMillis?.let { due -> due in dayStart..dayEnd || (!it.isCompleted && due < nowMillis) } == true }
        val highPriorityItems = items.filter { it.priority == TaskPriority.HIGH || it.priority == TaskPriority.URGENT }

        return PlannerDay(
            dateStartMillis = dayStart,
            dateEndMillis = dayEnd,
            tasks = tasksForDay.sortedBy { it.dueDateMillis ?: it.dateMillis },
            events = eventsForDay.sortedBy { it.dateMillis },
            dueItems = dueItems.sortedBy { it.dueDateMillis ?: it.dateMillis },
            highPriorityItems = highPriorityItems.sortedByDescending { it.priority.score }
        )
    }

    private fun includeItem(item: PlannerItem, filter: PlannerFilter): Boolean {
        if (!filter.showCompleted && item.isCompleted) return false
        if (filter.courseId != null && filter.courseId != item.courseId) return false
        return when (item.itemType) {
            PlannerItemType.TASK, PlannerItemType.COURSEWORK -> filter.showTasks
            PlannerItemType.ASSIGNMENT -> filter.showAssignments
            PlannerItemType.QUIZ -> filter.showQuizzes
            PlannerItemType.EXAM -> filter.showExams
            PlannerItemType.ANNOUNCEMENT -> filter.showAnnouncements
            PlannerItemType.MATERIAL -> filter.showMaterials
            else -> true
        }
    }

    private fun AcademicTask.toPlannerItem(): PlannerItem? {
        val date = dueDate ?: createdAtMillis
        return PlannerItem(
            id = "task_$id",
            title = title,
            description = description,
            courseName = courseName,
            itemType = PlannerItemType.TASK,
            sourceId = sourceId,
            sourceType = source,
            dateMillis = date,
            dueDateMillis = dueDate,
            priority = TaskPriority.fromScore(priority),
            isCompleted = isCompleted,
            originalLink = sourceLink
        )
    }

    private fun ClassroomEvent.toPlannerItem(): PlannerItem? {
        return PlannerItem(
            id = id,
            title = title,
            description = description,
            courseId = courseId,
            courseName = courseName,
            itemType = eventType.toPlannerItemType(),
            sourceId = sourceId,
            sourceType = source.name,
            dateMillis = dueDateMillis ?: eventTimeMillis,
            dueDateMillis = dueDateMillis,
            priority = priority,
            isCompleted = convertedToTask,
            originalLink = originalLink
        )
    }

    private fun ClassroomEventType.toPlannerItemType(): PlannerItemType {
        return when (this) {
            ClassroomEventType.ASSIGNMENT -> PlannerItemType.ASSIGNMENT
            ClassroomEventType.COURSEWORK -> PlannerItemType.COURSEWORK
            ClassroomEventType.QUIZ -> PlannerItemType.QUIZ
            ClassroomEventType.EXAM -> PlannerItemType.EXAM
            ClassroomEventType.ANNOUNCEMENT -> PlannerItemType.ANNOUNCEMENT
            ClassroomEventType.MATERIAL -> PlannerItemType.MATERIAL
            ClassroomEventType.REMINDER -> PlannerItemType.REMINDER
            ClassroomEventType.COMMENT,
            ClassroomEventType.TEACHER_FEEDBACK -> PlannerItemType.COMMENT
            ClassroomEventType.GRADE_UPDATE -> PlannerItemType.GRADE_UPDATE
            else -> PlannerItemType.UNKNOWN
        }
    }

    private fun dayBounds(timeMillis: Long): Pair<Long, Long> {
        val calendar = calendarAt(timeMillis)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        return start to (start + DAY_MILLIS - 1)
    }

    private fun calendarAt(timeMillis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = timeMillis }

    companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/planner/PlannerEvent.kt

```kt
package com.rochiee.classsync.bloc.planner

import com.rochiee.classsync.planner.PlannerFilter

sealed class PlannerEvent {
    object LoadToday : PlannerEvent()
    object LoadCurrentWeek : PlannerEvent()
    object LoadCurrentMonth : PlannerEvent()
    data class LoadRange(val startMillis: Long, val endMillis: Long) : PlannerEvent()
    data class SetFilter(val filter: PlannerFilter) : PlannerEvent()
    object ClearError : PlannerEvent()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/planner/PlannerState.kt

```kt
package com.rochiee.classsync.bloc.planner

import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.planner.PlannerFilter
import com.rochiee.classsync.planner.PlannerMonth
import com.rochiee.classsync.planner.PlannerWeek

data class PlannerState(
    val isLoading: Boolean = false,
    val today: PlannerDay? = null,
    val currentWeek: PlannerWeek? = null,
    val currentMonth: PlannerMonth? = null,
    val selectedRangeDays: List<PlannerDay> = emptyList(),
    val activeFilter: PlannerFilter = PlannerFilter(),
    val errorMessage: String? = null,
    val lastUpdatedMillis: Long? = null
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/planner/PlannerBlocViewModel.kt

```kt
package com.rochiee.classsync.bloc.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.usecase.planner.GetMonthPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetPlannerRangeUseCase
import com.rochiee.classsync.domain.usecase.planner.GetTodayPlannerUseCase
import com.rochiee.classsync.domain.usecase.planner.GetWeekPlannerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlannerBlocViewModel(
    private val getTodayPlannerUseCase: GetTodayPlannerUseCase,
    private val getWeekPlannerUseCase: GetWeekPlannerUseCase,
    private val getMonthPlannerUseCase: GetMonthPlannerUseCase,
    private val getPlannerRangeUseCase: GetPlannerRangeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PlannerState())
    val state: StateFlow<PlannerState> = _state.asStateFlow()

    fun onEvent(event: PlannerEvent) {
        when (event) {
            PlannerEvent.LoadToday -> loadToday()
            PlannerEvent.LoadCurrentWeek -> loadWeek()
            PlannerEvent.LoadCurrentMonth -> loadMonth()
            is PlannerEvent.LoadRange -> loadRange(event.startMillis, event.endMillis)
            is PlannerEvent.SetFilter -> {
                _state.update { it.copy(activeFilter = event.filter) }
            }
            PlannerEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadToday() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getTodayPlannerUseCase(_state.value.activeFilter) }
                .onSuccess { day ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            today = day,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadWeek() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getWeekPlannerUseCase(_state.value.activeFilter) }
                .onSuccess { week ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentWeek = week,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadMonth() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getMonthPlannerUseCase(_state.value.activeFilter) }
                .onSuccess { month ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentMonth = month,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun loadRange(startMillis: Long, endMillis: Long) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getPlannerRangeUseCase(startMillis, endMillis, _state.value.activeFilter) }
                .onSuccess { days ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedRangeDays = days,
                            lastUpdatedMillis = System.currentTimeMillis()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/widget/WidgetSummary.kt

```kt
package com.rochiee.classsync.widget

data class WidgetSummary(
    val todayTaskCount: Int,
    val urgentTaskCount: Int,
    val nextTaskTitle: String?,
    val nextTaskDueMillis: Long?,
    val lastUpdatedMillis: Long
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/widget/WidgetTaskFormatter.kt

```kt
package com.rochiee.classsync.widget

import java.text.DateFormat
import java.util.Date

class WidgetTaskFormatter {
    fun dueText(dueMillis: Long?): String {
        return if (dueMillis == null) {
            "No due time"
        } else {
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(dueMillis))
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/widget/WidgetDataProvider.kt

```kt
package com.rochiee.classsync.widget

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.TaskRepository
import java.util.Calendar

class WidgetDataProvider(
    private val taskRepository: TaskRepository
) {
    suspend fun getTodayTasks(): List<AcademicTask> {
        val bounds = todayBounds()
        return taskRepository.getTasksSnapshot().filter { task ->
            task.dueDate?.let { it in bounds.first..bounds.second } == true && !task.isCompleted
        }
    }

    suspend fun getUrgentTasks(): List<AcademicTask> {
        val now = System.currentTimeMillis()
        val urgentCutoff = now + 24L * 60L * 60L * 1000L
        return taskRepository.getTasksSnapshot().filter { task ->
            !task.isCompleted && task.dueDate?.let { it in now..urgentCutoff } == true
        }.sortedBy { it.dueDate }
    }

    suspend fun getNextUpcomingTask(): AcademicTask? {
        val now = System.currentTimeMillis()
        return taskRepository.getTasksSnapshot()
            .filter { !it.isCompleted && it.dueDate != null && it.dueDate >= now }
            .minByOrNull { it.dueDate ?: Long.MAX_VALUE }
    }

    suspend fun getWidgetSummary(): WidgetSummary {
        val todayTasks = getTodayTasks()
        val urgentTasks = getUrgentTasks()
        val nextTask = getNextUpcomingTask()
        return WidgetSummary(
            todayTaskCount = todayTasks.size,
            urgentTaskCount = urgentTasks.size,
            nextTaskTitle = nextTask?.title,
            nextTaskDueMillis = nextTask?.dueDate,
            lastUpdatedMillis = System.currentTimeMillis()
        )
    }

    private fun todayBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        return start to calendar.timeInMillis
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/widget/ClassSyncWidgetUpdater.kt

```kt
package com.rochiee.classsync.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.R
import kotlinx.coroutines.runBlocking

object ClassSyncWidgetUpdater {
    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, ClassSyncWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (widgetIds.isNotEmpty()) {
            updateWidgets(context, appWidgetManager, widgetIds)
        }
    }

    fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val app = context.applicationContext as ClassSyncApplication
        val summary = runBlocking {
            app.container.widgetDataProvider.getWidgetSummary()
        }
        val formatter = app.container.widgetTaskFormatter

        appWidgetIds.forEach { widgetId ->
            val views = RemoteViews(context.packageName, R.layout.classsync_widget_layout).apply {
                setTextViewText(R.id.widgetTitle, "ClassSync")
                setTextViewText(R.id.widgetTodayCount, "Today: ${summary.todayTaskCount}")
                setTextViewText(R.id.widgetUrgentCount, "Urgent: ${summary.urgentTaskCount}")
                setTextViewText(
                    R.id.widgetNextTaskTitle,
                    summary.nextTaskTitle ?: "No academic tasks yet"
                )
                setTextViewText(
                    R.id.widgetNextTaskDue,
                    formatter.dueText(summary.nextTaskDueMillis)
                )
                setOnClickPendingIntent(
                    R.id.widgetRefreshButton,
                    refreshPendingIntent(context)
                )
            }
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    private fun refreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ClassSyncWidgetProvider::class.java).apply {
            action = ClassSyncWidgetProvider.ACTION_REFRESH
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/widget/ClassSyncWidgetProvider.kt

```kt
package com.rochiee.classsync.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent

class ClassSyncWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        ClassSyncWidgetUpdater.updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH || intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            ClassSyncWidgetUpdater.updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.rochiee.classsync.widget.ACTION_REFRESH"
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/res/xml/classsync_widget_info.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="180dp"
    android:minHeight="110dp"
    android:updatePeriodMillis="0"
    android:initialLayout="@layout/classsync_widget_layout"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen" />

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/res/layout/classsync_widget_layout.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FFF7E9"
    android:orientation="vertical"
    android:padding="12dp">

    <TextView
        android:id="@+id/widgetTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="ClassSync"
        android:textColor="#1B1B1B"
        android:textSize="18sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/widgetTodayCount"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="Today: 0"
        android:textColor="#1B1B1B"
        android:textSize="14sp" />

    <TextView
        android:id="@+id/widgetUrgentCount"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Urgent: 0"
        android:textColor="#1B1B1B"
        android:textSize="14sp" />

    <TextView
        android:id="@+id/widgetNextTaskTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="No academic tasks yet"
        android:textColor="#1B1B1B"
        android:textSize="14sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/widgetNextTaskDue"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="No due time"
        android:textColor="#5B5B5B"
        android:textSize="12sp" />

    <Button
        android:id="@+id/widgetRefreshButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="Refresh" />
</LinearLayout>

```

