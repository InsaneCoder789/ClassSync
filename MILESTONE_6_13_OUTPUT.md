# ClassSync Backend Completion Output

Deleted file: /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/database/AppDatabase.kt
Final state: deleted during cleanup because it was unused and redundant with ClassSyncDatabase.

## /Users/rohanc/AndroidStudioProjects/classsync/app/build.gradle.kts

```kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.rochiee.classsync"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rochiee.classsync"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.google.api.client.android)
    implementation(libs.google.api.services.gmail)
    implementation(libs.google.api.services.classroom)
    implementation(libs.google.oauth.client.jetty)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/gradle/libs.versions.toml

```toml
[versions]
agp = "8.13.2"
kotlin = "2.0.21"
coreKtx = "1.18.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
lifecycleRuntimeKtx = "2.10.0"
activityCompose = "1.13.0"
composeBom = "2024.09.00"
room = "2.6.1"
ksp = "2.0.21-1.0.27"
credentials = "1.3.0"
googleid = "1.1.1"
google-api-client = "2.2.0"
google-oauth-client = "1.34.1"
google-api-services-gmail = "v1-rev20220404-2.0.0"
google-api-services-classroom = "v1-rev20240617-2.0.0"
work = "2.10.1"
datastore = "1.1.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-credentials = { group = "androidx.credentials", name = "credentials", version.ref = "credentials" }
androidx-credentials-play-services-auth = { group = "androidx.credentials", name = "credentials-play-services-auth", version.ref = "credentials" }
googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }
google-api-client-android = { group = "com.google.api-client", name = "google-api-client-android", version.ref = "google-api-client" }
google-api-services-gmail = { group = "com.google.apis", name = "google-api-services-gmail", version.ref = "google-api-services-gmail" }
google-api-services-classroom = { group = "com.google.apis", name = "google-api-services-classroom", version.ref = "google-api-services-classroom" }
google-oauth-client-jetty = { group = "com.google.oauth-client", name = "google-oauth-client-jetty", version.ref = "google-oauth-client" }
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "work" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }

```

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
    </application>

</manifest>

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/ClassSyncApplication.kt

```kt
package com.rochiee.classsync

import android.app.Application
import com.rochiee.classsync.di.AppContainer
import com.rochiee.classsync.di.AppContainerImpl
import com.rochiee.classsync.reminder.ReminderNotificationHelper

class ClassSyncApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        ReminderNotificationHelper.ensureChannel(this)
    }
}

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
                
                TaskBackendTestScreen(
                    taskState = taskState,
                    onTaskEvent = taskViewModel::onEvent,
                    authState = authState,
                    onAuthEvent = authViewModel::onEvent,
                    syncState = syncState,
                    onSyncEvent = syncViewModel::onEvent,
                    settingsState = settingsState,
                    onSettingsEvent = settingsViewModel::onEvent
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
    onSettingsEvent: (SettingsEvent) -> Unit
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

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/auth/GoogleAuthManager.kt

```kt
package com.rochiee.classsync.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoogleAuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // TODO: Replace with your actual web client ID from Google Cloud Console
    private val webClientId = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"

    suspend fun signIn(activityContext: Context) {
        _authState.value = AuthState.Loading
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                _authState.value = AuthState.Authenticated(
                    email = credential.id,
                    displayName = credential.displayName,
                    idToken = credential.idToken
                )
            } else {
                _authState.value = AuthState.Error("Unexpected credential type")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _authState.value = AuthState.Unauthenticated
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign-out failed")
        }
    }

    fun checkAuthState() {
        // Simple placeholder for Milestone 3
        if (_authState.value == AuthState.Idle) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun isSignedIn(): Boolean = authState.value is AuthState.Authenticated
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/auth/AuthBlocViewModel.kt

```kt
package com.rochiee.classsync.bloc.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.auth.AuthState
import com.rochiee.classsync.auth.GoogleAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthBlocViewModel(
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        googleAuthManager.authState
            .onEach { authState ->
                _state.update {
                    when (authState) {
                        is AuthState.Authenticated -> it.copy(
                            isSignedIn = true,
                            isLoading = false,
                            userEmail = authState.email,
                            displayName = authState.displayName,
                            errorMessage = null
                        )
                        is AuthState.Error -> it.copy(
                            isSignedIn = false,
                            isLoading = false,
                            errorMessage = authState.message
                        )
                        AuthState.Loading -> it.copy(isLoading = true)
                        AuthState.Unauthenticated, AuthState.Idle -> it.copy(
                            isSignedIn = false,
                            isLoading = false,
                            userEmail = null,
                            displayName = null
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.CheckAuthState -> googleAuthManager.checkAuthState()
            is AuthEvent.SignIn -> {
                viewModelScope.launch {
                    googleAuthManager.signIn(event.context)
                }
            }
            AuthEvent.SignOut -> {
                viewModelScope.launch {
                    googleAuthManager.signOut()
                }
            }
            AuthEvent.ClearAuthError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/task/TaskEvent.kt

```kt
package com.rochiee.classsync.bloc.task

import com.rochiee.classsync.domain.model.AcademicTask

sealed class TaskEvent {
    object LoadTasks : TaskEvent()
    data class AddTaskFromRawText(val rawText: String, val courseName: String) : TaskEvent()
    data class AddManualTask(
        val title: String, 
        val description: String, 
        val courseName: String, 
        val dueDateMillis: Long
    ) : TaskEvent()
    data class ToggleTaskCompletion(val taskId: Int, val isCompleted: Boolean) : TaskEvent()
    data class DeleteTask(val task: AcademicTask) : TaskEvent()
    data class SimulateNotificationTask(
        val packageName: String,
        val title: String,
        val text: String
    ) : TaskEvent()
    object SyncGmailTasks : TaskEvent()
    object SyncClassroomTasks : TaskEvent()
    
    // Milestone 6: WorkManager Events
    object ScheduleBackgroundSync : TaskEvent()
    object CancelBackgroundSync : TaskEvent()
    object RunOneTimeFullSync : TaskEvent()
    object OpenNotificationAccessSettings : TaskEvent()
    object ExportTasksCsv : TaskEvent()
    object ExportTasksJson : TaskEvent()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/task/TaskBlocViewModel.kt

```kt
package com.rochiee.classsync.bloc.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.usecase.export.ExportTasksCsvUseCase
import com.rochiee.classsync.domain.usecase.export.ExportTasksJsonUseCase
import com.rochiee.classsync.domain.usecase.gmail.SyncGmailTasksUseCase
import com.rochiee.classsync.domain.usecase.notification.OpenNotificationAccessSettingsUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.synclog.AddSyncLogUseCase
import com.rochiee.classsync.domain.usecase.task.AddManualTaskUseCase
import com.rochiee.classsync.domain.usecase.task.DeleteTaskUseCase
import com.rochiee.classsync.domain.usecase.task.MarkTaskCompletedUseCase
import com.rochiee.classsync.domain.usecase.task.ObserveTasksUseCase
import com.rochiee.classsync.domain.usecase.worker.CancelBackgroundSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.RunOneTimeFullSyncUseCase
import com.rochiee.classsync.domain.usecase.worker.ScheduleBackgroundSyncUseCase
import com.rochiee.classsync.taskengine.DeadlineParser
import com.rochiee.classsync.taskengine.NotificationTaskParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskBlocViewModel(
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val addManualTaskUseCase: AddManualTaskUseCase,
    private val markTaskCompletedUseCase: MarkTaskCompletedUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val syncGmailTasksUseCase: SyncGmailTasksUseCase,
    private val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase,
    private val scheduleBackgroundSyncUseCase: ScheduleBackgroundSyncUseCase,
    private val cancelBackgroundSyncUseCase: CancelBackgroundSyncUseCase,
    private val runOneTimeFullSyncUseCase: RunOneTimeFullSyncUseCase,
    private val openNotificationAccessSettingsUseCase: OpenNotificationAccessSettingsUseCase,
    private val addSyncLogUseCase: AddSyncLogUseCase,
    private val exportTasksCsvUseCase: ExportTasksCsvUseCase,
    private val exportTasksJsonUseCase: ExportTasksJsonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        onEvent(TaskEvent.LoadTasks)
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.LoadTasks -> {
                observeTasks()
            }
            is TaskEvent.AddTaskFromRawText -> {
                addTaskFromRawText(event.rawText, event.courseName)
            }
            is TaskEvent.AddManualTask -> {
                addManualTask(event.title, event.description, event.courseName, event.dueDateMillis)
            }
            is TaskEvent.ToggleTaskCompletion -> {
                toggleCompletion(event.taskId, event.isCompleted)
            }
            is TaskEvent.DeleteTask -> {
                deleteTask(event.task)
            }
            is TaskEvent.SimulateNotificationTask -> {
                simulateNotification(event.packageName, event.title, event.text)
            }
            TaskEvent.SyncGmailTasks -> {
                syncGmailTasks()
            }
            TaskEvent.SyncClassroomTasks -> {
                syncClassroomTasks()
            }
            TaskEvent.ScheduleBackgroundSync -> {
                scheduleBackgroundSync()
            }
            TaskEvent.CancelBackgroundSync -> {
                cancelBackgroundSync()
            }
            TaskEvent.RunOneTimeFullSync -> {
                runOneTimeFullSync()
            }
            TaskEvent.OpenNotificationAccessSettings -> {
                openNotificationAccessSettings()
            }
            TaskEvent.ExportTasksCsv -> {
                exportTasksCsv()
            }
            TaskEvent.ExportTasksJson -> {
                exportTasksJson()
            }
        }
    }

    private fun observeTasks() {
        _state.update { it.copy(isLoading = true) }
        observeTasksUseCase()
            .onEach { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
            .catch { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun addTaskFromRawText(rawText: String, courseName: String) {
        val lines = rawText.lines()
        val title = lines.find { it.contains("posted:", ignoreCase = true) }
            ?.substringAfter("posted:")?.trim() ?: "Raw Task"
        
        viewModelScope.launch {
            addManualTaskUseCase(
                AcademicTask(
                    title = title,
                    description = rawText,
                    courseName = courseName,
                    dueDate = DeadlineParser.parse(rawText),
                    source = "Raw Text"
                )
            )
        }
    }

    private fun addManualTask(title: String, description: String, courseName: String, dueDateMillis: Long) {
        viewModelScope.launch {
            addManualTaskUseCase(
                AcademicTask(
                    title = title,
                    description = description,
                    courseName = courseName,
                    dueDate = dueDateMillis,
                    source = "Manual"
                )
            )
        }
    }

    private fun toggleCompletion(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            markTaskCompletedUseCase(taskId, isCompleted)
        }
    }

    private fun deleteTask(task: AcademicTask) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    private fun simulateNotification(packageName: String, title: String, text: String) {
        viewModelScope.launch {
            val task = NotificationTaskParser.parse(packageName, title, text)
            task?.let {
                if (_state.value.tasks.none { t -> t.title == it.title && t.description == it.description }) {
                    addManualTaskUseCase(it)
                }
            }
        }
    }

    private fun syncGmailTasks() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                syncGmailTasksUseCase()
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun syncClassroomTasks() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                syncClassroomCourseworkUseCase()
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun scheduleBackgroundSync() {
        viewModelScope.launch {
            try {
                scheduleBackgroundSyncUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "WORK_SCHEDULER",
                        status = "SUCCESS",
                        message = "Scheduled background sync jobs.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun cancelBackgroundSync() {
        viewModelScope.launch {
            try {
                cancelBackgroundSyncUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "WORK_SCHEDULER",
                        status = "SUCCESS",
                        message = "Cancelled background sync jobs.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun runOneTimeFullSync() {
        viewModelScope.launch {
            try {
                runOneTimeFullSyncUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "WORK_SCHEDULER",
                        status = "SUCCESS",
                        message = "Queued one-time full sync.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun openNotificationAccessSettings() {
        openNotificationAccessSettingsUseCase()
    }

    private fun exportTasksCsv() {
        viewModelScope.launch {
            try {
                val file = exportTasksCsvUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "EXPORT",
                        status = "SUCCESS",
                        message = "Exported CSV to ${file.absolutePath}",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    private fun exportTasksJson() {
        viewModelScope.launch {
            try {
                val file = exportTasksJsonUseCase()
                addSyncLogUseCase(
                    SyncLog(
                        source = "EXPORT",
                        status = "SUCCESS",
                        message = "Exported JSON to ${file.absolutePath}",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message) }
            }
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/sync/SyncEvent.kt

```kt
package com.rochiee.classsync.bloc.sync

sealed class SyncEvent {
    object ObserveLogs : SyncEvent()
    object RunManualFullSync : SyncEvent()
    object RunGmailSync : SyncEvent()
    object RunClassroomSync : SyncEvent()
    object ClearLogs : SyncEvent()
    object ClearError : SyncEvent()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/sync/SyncState.kt

```kt
package com.rochiee.classsync.bloc.sync

import com.rochiee.classsync.domain.model.SyncLog

data class SyncState(
    val isSyncing: Boolean = false,
    val logs: List<SyncLog> = emptyList(),
    val lastSyncMillis: Long? = null,
    val errorMessage: String? = null
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/sync/SyncBlocViewModel.kt

```kt
package com.rochiee.classsync.bloc.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
import com.rochiee.classsync.domain.usecase.gmail.SyncGmailTasksUseCase
import com.rochiee.classsync.domain.usecase.synclog.ClearSyncLogsUseCase
import com.rochiee.classsync.domain.usecase.synclog.ObserveSyncLogsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SyncBlocViewModel(
    private val observeSyncLogsUseCase: ObserveSyncLogsUseCase,
    private val clearSyncLogsUseCase: ClearSyncLogsUseCase,
    private val syncGmailTasksUseCase: SyncGmailTasksUseCase,
    private val syncClassroomCoursesUseCase: SyncClassroomCoursesUseCase,
    private val syncClassroomCourseworkUseCase: SyncClassroomCourseworkUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SyncState())
    val state: StateFlow<SyncState> = _state.asStateFlow()

    init {
        onEvent(SyncEvent.ObserveLogs)
    }

    fun onEvent(event: SyncEvent) {
        when (event) {
            SyncEvent.ObserveLogs -> observeLogs()
            SyncEvent.RunManualFullSync -> runManualFullSync()
            SyncEvent.RunGmailSync -> runGmailSync()
            SyncEvent.RunClassroomSync -> runClassroomSync()
            SyncEvent.ClearLogs -> clearLogs()
            SyncEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeLogs() {
        observeSyncLogsUseCase()
            .onEach { logs ->
                _state.update {
                    it.copy(
                        logs = logs,
                        lastSyncMillis = logs.maxOfOrNull { log -> log.timestamp }
                    )
                }
            }
            .catch { error ->
                _state.update { it.copy(errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun runManualFullSync() {
        _state.update { it.copy(isSyncing = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                syncClassroomCoursesUseCase()
                syncClassroomCourseworkUseCase()
                syncGmailTasksUseCase()
                _state.update { it.copy(isSyncing = false) }
            } catch (error: Exception) {
                _state.update { it.copy(isSyncing = false, errorMessage = error.message) }
            }
        }
    }

    private fun runGmailSync() {
        _state.update { it.copy(isSyncing = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                syncGmailTasksUseCase()
                _state.update { it.copy(isSyncing = false) }
            } catch (error: Exception) {
                _state.update { it.copy(isSyncing = false, errorMessage = error.message) }
            }
        }
    }

    private fun runClassroomSync() {
        _state.update { it.copy(isSyncing = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                syncClassroomCoursesUseCase()
                syncClassroomCourseworkUseCase()
                _state.update { it.copy(isSyncing = false) }
            } catch (error: Exception) {
                _state.update { it.copy(isSyncing = false, errorMessage = error.message) }
            }
        }
    }

    private fun clearLogs() {
        viewModelScope.launch {
            clearSyncLogsUseCase()
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/settings/SettingsEvent.kt

```kt
package com.rochiee.classsync.bloc.settings

sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class SetBackgroundSyncEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetGmailSyncEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetClassroomSyncEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetNotificationParsingEnabled(val enabled: Boolean) : SettingsEvent()
    data class SetDefaultReminderHours(val hours: Int) : SettingsEvent()
    object ClearError : SettingsEvent()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/settings/SettingsState.kt

```kt
package com.rochiee.classsync.bloc.settings

data class SettingsState(
    val isLoading: Boolean = false,
    val backgroundSyncEnabled: Boolean = true,
    val gmailSyncEnabled: Boolean = true,
    val classroomSyncEnabled: Boolean = true,
    val notificationParsingEnabled: Boolean = true,
    val defaultReminderHours: Int = 2,
    val lastSyncTimeMillis: Long? = null,
    val errorMessage: String? = null
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/bloc/settings/SettingsBlocViewModel.kt

```kt
package com.rochiee.classsync.bloc.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rochiee.classsync.domain.usecase.settings.ObserveSettingsUseCase
import com.rochiee.classsync.domain.usecase.settings.SetBackgroundSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetClassroomSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetDefaultReminderHoursUseCase
import com.rochiee.classsync.domain.usecase.settings.SetGmailSyncEnabledUseCase
import com.rochiee.classsync.domain.usecase.settings.SetNotificationParsingEnabledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsBlocViewModel(
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val setBackgroundSyncEnabledUseCase: SetBackgroundSyncEnabledUseCase,
    private val setGmailSyncEnabledUseCase: SetGmailSyncEnabledUseCase,
    private val setClassroomSyncEnabledUseCase: SetClassroomSyncEnabledUseCase,
    private val setNotificationParsingEnabledUseCase: SetNotificationParsingEnabledUseCase,
    private val setDefaultReminderHoursUseCase: SetDefaultReminderHoursUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(isLoading = true))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        onEvent(SettingsEvent.LoadSettings)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.LoadSettings -> observeSettings()
            is SettingsEvent.SetBackgroundSyncEnabled -> updateSetting { setBackgroundSyncEnabledUseCase(event.enabled) }
            is SettingsEvent.SetGmailSyncEnabled -> updateSetting { setGmailSyncEnabledUseCase(event.enabled) }
            is SettingsEvent.SetClassroomSyncEnabled -> updateSetting { setClassroomSyncEnabledUseCase(event.enabled) }
            is SettingsEvent.SetNotificationParsingEnabled -> updateSetting { setNotificationParsingEnabledUseCase(event.enabled) }
            is SettingsEvent.SetDefaultReminderHours -> updateSetting { setDefaultReminderHoursUseCase(event.hours) }
            SettingsEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeSettings() {
        observeSettingsUseCase()
            .onEach { settings ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        backgroundSyncEnabled = settings.backgroundSyncEnabled,
                        gmailSyncEnabled = settings.gmailSyncEnabled,
                        classroomSyncEnabled = settings.classroomSyncEnabled,
                        notificationParsingEnabled = settings.notificationParsingEnabled,
                        defaultReminderHours = settings.defaultReminderHours,
                        lastSyncTimeMillis = settings.lastSyncTimeMillis,
                        errorMessage = null
                    )
                }
            }
            .catch { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun updateSetting(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (error: Exception) {
                _state.update { it.copy(errorMessage = error.message) }
            }
        }
    }
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
import com.rochiee.classsync.data.repository.ClassroomRepositoryImpl
import com.rochiee.classsync.data.repository.GmailRepositoryImpl
import com.rochiee.classsync.data.repository.SettingsRepositoryImpl
import com.rochiee.classsync.data.repository.SyncLogRepositoryImpl
import com.rochiee.classsync.data.repository.TaskRepositoryImpl
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.GmailRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCoursesUseCase
import com.rochiee.classsync.domain.usecase.classroom.SyncClassroomCourseworkUseCase
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
import com.rochiee.classsync.export.TaskCsvExporter
import com.rochiee.classsync.export.TaskExportManager
import com.rochiee.classsync.export.TaskJsonExporter
import com.rochiee.classsync.reminder.ReminderScheduler

interface AppContainer {
    val database: ClassSyncDatabase
    val taskRepository: TaskRepository
    val settingsRepository: SettingsRepository
    val syncLogRepository: SyncLogRepository
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
        TaskRepositoryImpl(database.taskDao, reminderScheduler)
    }

    private val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context.applicationContext)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsDataStore)
    }

    override val syncLogRepository: SyncLogRepository by lazy {
        SyncLogRepositoryImpl(database.syncLogDao)
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
            settingsRepository,
            setLastSyncTimeUseCase
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
            settingsRepository,
            setLastSyncTimeUseCase
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
import com.rochiee.classsync.data.local.dao.NotificationDao
import com.rochiee.classsync.data.local.dao.SyncLogDao
import com.rochiee.classsync.data.local.dao.TaskDao
import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.data.local.entity.NotificationEntity
import com.rochiee.classsync.data.local.entity.SyncLogEntity
import com.rochiee.classsync.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, NotificationEntity::class, CourseEntity::class, SyncLogEntity::class],
    version = 5,
    exportSchema = false
)
abstract class ClassSyncDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val notificationDao: NotificationDao
    abstract val courseDao: CourseDao
    abstract val syncLogDao: SyncLogDao

    companion object {
        const val DATABASE_NAME = "classsync_db"
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/dao/TaskDao.kt

```kt
package com.rochiee.classsync.data.local.dao

import androidx.room.*
import com.rochiee.classsync.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksSnapshot(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/dao/SyncLogDao.kt

```kt
package com.rochiee.classsync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rochiee.classsync.data.local.entity.SyncLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncLogDao {
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC")
    fun observeLogs(): Flow<List<SyncLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SyncLogEntity)

    @Query("DELETE FROM sync_logs")
    suspend fun clearLogs()

    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLog(): SyncLogEntity?
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/entity/TaskEntity.kt

```kt
package com.rochiee.classsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val courseName: String,
    val isCompleted: Boolean,
    val dueDate: Long?,
    val priority: Int,
    val source: String,
    val sourceId: String? = null,
    val sourceLink: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/entity/SyncLogEntity.kt

```kt
package com.rochiee.classsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val source: String,
    val status: String,
    val message: String,
    val timestamp: Long
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/mapper/TaskMapper.kt

```kt
package com.rochiee.classsync.data.local.mapper

import com.rochiee.classsync.data.local.entity.TaskEntity
import com.rochiee.classsync.domain.model.AcademicTask

fun TaskEntity.toAcademicTask(): AcademicTask {
    return AcademicTask(
        id = id,
        title = title,
        description = description,
        courseName = courseName,
        isCompleted = isCompleted,
        dueDate = dueDate,
        priority = priority,
        source = source,
        sourceId = sourceId,
        sourceLink = sourceLink,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

fun AcademicTask.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        courseName = courseName,
        isCompleted = isCompleted,
        dueDate = dueDate,
        priority = priority,
        source = source,
        sourceId = sourceId,
        sourceLink = sourceLink,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/mapper/SyncLogMapper.kt

```kt
package com.rochiee.classsync.data.local.mapper

import com.rochiee.classsync.data.local.entity.SyncLogEntity
import com.rochiee.classsync.domain.model.SyncLog

fun SyncLogEntity.toDomain(): SyncLog {
    return SyncLog(
        id = id,
        source = source,
        status = status,
        message = message,
        timestamp = timestamp
    )
}

fun SyncLog.toEntity(): SyncLogEntity {
    return SyncLogEntity(
        id = id,
        source = source,
        status = status,
        message = message,
        timestamp = timestamp
    )
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/local/preferences/SettingsDataStore.kt

```kt
package com.rochiee.classsync.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rochiee.classsync.domain.model.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "classsync_settings")

class SettingsDataStore(private val context: Context) {
    val settings: Flow<SettingsPreferences> = context.settingsDataStore.data.map { preferences ->
        SettingsPreferences(
            backgroundSyncEnabled = preferences[Keys.backgroundSyncEnabled] ?: true,
            gmailSyncEnabled = preferences[Keys.gmailSyncEnabled] ?: true,
            classroomSyncEnabled = preferences[Keys.classroomSyncEnabled] ?: true,
            notificationParsingEnabled = preferences[Keys.notificationParsingEnabled] ?: true,
            defaultReminderHours = preferences[Keys.defaultReminderHours] ?: 2,
            lastSyncTimeMillis = preferences[Keys.lastSyncTimeMillis]
        )
    }

    suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        update { it[Keys.backgroundSyncEnabled] = enabled }
    }

    suspend fun setGmailSyncEnabled(enabled: Boolean) {
        update { it[Keys.gmailSyncEnabled] = enabled }
    }

    suspend fun setClassroomSyncEnabled(enabled: Boolean) {
        update { it[Keys.classroomSyncEnabled] = enabled }
    }

    suspend fun setNotificationParsingEnabled(enabled: Boolean) {
        update { it[Keys.notificationParsingEnabled] = enabled }
    }

    suspend fun setDefaultReminderHours(hours: Int) {
        update { it[Keys.defaultReminderHours] = hours }
    }

    suspend fun setLastSyncTimeMillis(timeMillis: Long) {
        update { it[Keys.lastSyncTimeMillis] = timeMillis }
    }

    private suspend fun update(block: suspend (MutablePreferencesAdapter) -> Unit) {
        context.settingsDataStore.edit { preferences ->
            block(MutablePreferencesAdapter(preferences))
        }
    }

    private class MutablePreferencesAdapter(
        private val preferences: androidx.datastore.preferences.core.MutablePreferences
    ) {
        operator fun <T> set(key: Preferences.Key<T>, value: T) {
            preferences[key] = value
        }
    }

    private object Keys {
        val backgroundSyncEnabled = booleanPreferencesKey("background_sync_enabled")
        val gmailSyncEnabled = booleanPreferencesKey("gmail_sync_enabled")
        val classroomSyncEnabled = booleanPreferencesKey("classroom_sync_enabled")
        val notificationParsingEnabled = booleanPreferencesKey("notification_parsing_enabled")
        val defaultReminderHours = intPreferencesKey("default_reminder_hours")
        val lastSyncTimeMillis = longPreferencesKey("last_sync_time_millis")
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
import com.rochiee.classsync.taskengine.NotificationTaskParser
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

            val task = NotificationTaskParser.parse(packageName, title, text)
            task?.let {
                repository.addTask(it)
                syncLogRepository.addLog(
                    SyncLog(
                        source = "NOTIFICATION",
                        status = "SUCCESS",
                        message = "Parsed notification into task: ${it.title}",
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val dao: TaskDao,
    private val reminderScheduler: ReminderScheduler
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
        } else {
            val insertedId = dao.insertTask(taskWithTimestamps.toTaskEntity()).toInt()
            reminderScheduler.schedule(taskWithTimestamps.copy(id = insertedId))
        }
    }

    override suspend fun updateTask(task: AcademicTask) {
        val updatedTask = task.copy(updatedAtMillis = System.currentTimeMillis())
        dao.updateTask(updatedTask.toTaskEntity())
        reminderScheduler.schedule(updatedTask)
    }

    override suspend fun deleteTask(task: AcademicTask) {
        reminderScheduler.cancel(task)
        dao.deleteTask(task.toTaskEntity())
    }

    override suspend fun getTaskById(id: Int): AcademicTask? {
        return dao.getTaskById(id)?.toAcademicTask()
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/repository/SyncLogRepositoryImpl.kt

```kt
package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.dao.SyncLogDao
import com.rochiee.classsync.data.local.mapper.toDomain
import com.rochiee.classsync.data.local.mapper.toEntity
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.repository.SyncLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncLogRepositoryImpl(
    private val syncLogDao: SyncLogDao
) : SyncLogRepository {
    override fun observeLogs(): Flow<List<SyncLog>> {
        return syncLogDao.observeLogs().map { logs -> logs.map { it.toDomain() } }
    }

    override suspend fun addLog(log: SyncLog) {
        syncLogDao.insertLog(log.toEntity())
    }

    override suspend fun clearLogs() {
        syncLogDao.clearLogs()
    }

    override suspend fun getLatestLog(): SyncLog? {
        return syncLogDao.getLatestLog()?.toDomain()
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/data/repository/SettingsRepositoryImpl.kt

```kt
package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.local.preferences.SettingsDataStore
import com.rochiee.classsync.domain.model.SettingsPreferences
import com.rochiee.classsync.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    override fun observeSettings(): Flow<SettingsPreferences> = settingsDataStore.settings

    override suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        settingsDataStore.setBackgroundSyncEnabled(enabled)
    }

    override suspend fun setGmailSyncEnabled(enabled: Boolean) {
        settingsDataStore.setGmailSyncEnabled(enabled)
    }

    override suspend fun setClassroomSyncEnabled(enabled: Boolean) {
        settingsDataStore.setClassroomSyncEnabled(enabled)
    }

    override suspend fun setNotificationParsingEnabled(enabled: Boolean) {
        settingsDataStore.setNotificationParsingEnabled(enabled)
    }

    override suspend fun setDefaultReminderHours(hours: Int) {
        settingsDataStore.setDefaultReminderHours(hours)
    }

    override suspend fun setLastSyncTimeMillis(timeMillis: Long) {
        settingsDataStore.setLastSyncTimeMillis(timeMillis)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/AcademicTask.kt

```kt
package com.rochiee.classsync.domain.model

data class AcademicTask(
    val id: Int = 0,
    val title: String,
    val description: String,
    val courseName: String,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val priority: Int = 0,
    val source: String = "Manual",
    val sourceId: String? = null,
    val sourceLink: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/SyncLog.kt

```kt
package com.rochiee.classsync.domain.model

data class SyncLog(
    val id: Int = 0,
    val source: String,
    val status: String,
    val message: String,
    val timestamp: Long
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/model/SettingsPreferences.kt

```kt
package com.rochiee.classsync.domain.model

data class SettingsPreferences(
    val backgroundSyncEnabled: Boolean = true,
    val gmailSyncEnabled: Boolean = true,
    val classroomSyncEnabled: Boolean = true,
    val notificationParsingEnabled: Boolean = true,
    val defaultReminderHours: Int = 2,
    val lastSyncTimeMillis: Long? = null
)

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/repository/TaskRepository.kt

```kt
package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.AcademicTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<AcademicTask>>
    suspend fun getTasksSnapshot(): List<AcademicTask>
    suspend fun addTask(task: AcademicTask)
    suspend fun updateTask(task: AcademicTask)
    suspend fun deleteTask(task: AcademicTask)
    suspend fun getTaskById(id: Int): AcademicTask?
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/repository/SyncLogRepository.kt

```kt
package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.SyncLog
import kotlinx.coroutines.flow.Flow

interface SyncLogRepository {
    fun observeLogs(): Flow<List<SyncLog>>
    suspend fun addLog(log: SyncLog)
    suspend fun clearLogs()
    suspend fun getLatestLog(): SyncLog?
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/repository/SettingsRepository.kt

```kt
package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.domain.model.SettingsPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<SettingsPreferences>
    suspend fun setBackgroundSyncEnabled(enabled: Boolean)
    suspend fun setGmailSyncEnabled(enabled: Boolean)
    suspend fun setClassroomSyncEnabled(enabled: Boolean)
    suspend fun setNotificationParsingEnabled(enabled: Boolean)
    suspend fun setDefaultReminderHours(hours: Int)
    suspend fun setLastSyncTimeMillis(timeMillis: Long)
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/task/DeleteTaskUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.task

import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: AcademicTask) {
        repository.deleteTask(task)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/gmail/SyncGmailTasksUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.gmail

import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.repository.GmailRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.taskengine.GmailTaskParser
import kotlinx.coroutines.flow.first

class SyncGmailTasksUseCase(
    private val gmailRepository: GmailRepository,
    private val taskRepository: TaskRepository,
    private val syncLogRepository: SyncLogRepository,
    private val settingsRepository: SettingsRepository,
    private val setLastSyncTimeUseCase: SetLastSyncTimeUseCase
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
            messages.forEach { message ->
                val task = GmailTaskParser.parse(message)
                task?.let {
                    taskRepository.addTask(it)
                    importedCount += 1
                }
            }

            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL",
                    status = "SUCCESS",
                    message = "Imported $importedCount tasks from ${messages.size} Gmail messages.",
                    timestamp = System.currentTimeMillis()
                )
            )
            setLastSyncTimeUseCase(System.currentTimeMillis())
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

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/classroom/SyncClassroomCoursesUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.classroom

import com.rochiee.classsync.data.local.entity.CourseEntity
import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import kotlinx.coroutines.flow.first

class SyncClassroomCoursesUseCase(
    private val classroomRepository: ClassroomRepository,
    private val syncLogRepository: SyncLogRepository,
    private val settingsRepository: SettingsRepository,
    private val setLastSyncTimeUseCase: SetLastSyncTimeUseCase
) {
    suspend operator fun invoke() {
        try {
            if (!settingsRepository.observeSettings().first().classroomSyncEnabled) {
                syncLogRepository.addLog(
                    SyncLog(
                        source = "CLASSROOM_COURSES",
                        status = "SKIPPED",
                        message = "Skipped Classroom course sync because Classroom sync is disabled in settings.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                return
            }
            val remoteCourses = classroomRepository.fetchRemoteCourses()
            val entities = remoteCourses.map { dto ->
                CourseEntity(
                    courseId = dto.courseId,
                    name = dto.name,
                    section = dto.section,
                    room = dto.room,
                    descriptionHeading = dto.descriptionHeading,
                    teacherName = dto.teacherName,
                    courseState = dto.courseState
                )
            }
            classroomRepository.saveCourses(entities)
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_COURSES",
                    status = "SUCCESS",
                    message = "Saved ${entities.size} active courses.",
                    timestamp = System.currentTimeMillis()
                )
            )
            setLastSyncTimeUseCase(System.currentTimeMillis())
        } catch (error: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_COURSES",
                    status = "ERROR",
                    message = error.message ?: "Classroom course sync failed.",
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
import com.rochiee.classsync.domain.repository.ClassroomRepository
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.domain.repository.SyncLogRepository
import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.domain.usecase.settings.SetLastSyncTimeUseCase
import com.rochiee.classsync.taskengine.ClassroomTaskParser
import kotlinx.coroutines.flow.first

class SyncClassroomCourseworkUseCase(
    private val classroomRepository: ClassroomRepository,
    private val taskRepository: TaskRepository,
    private val syncLogRepository: SyncLogRepository,
    private val settingsRepository: SettingsRepository,
    private val setLastSyncTimeUseCase: SetLastSyncTimeUseCase
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

            remoteCourses.forEach { courseDto ->
                val courseWorkList = classroomRepository.fetchRemoteCourseWork(courseDto.courseId)
                courseWorkList.forEach { workDto ->
                    val task = ClassroomTaskParser.parse(workDto, courseDto.name)
                    taskRepository.addTask(task)
                    importedCount += 1
                }
            }

            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_TASKS",
                    status = "SUCCESS",
                    message = "Imported $importedCount coursework tasks from ${remoteCourses.size} courses.",
                    timestamp = System.currentTimeMillis()
                )
            )
            setLastSyncTimeUseCase(System.currentTimeMillis())
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

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/synclog/ObserveSyncLogsUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.synclog

import com.rochiee.classsync.domain.repository.SyncLogRepository

class ObserveSyncLogsUseCase(
    private val repository: SyncLogRepository
) {
    operator fun invoke() = repository.observeLogs()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/synclog/AddSyncLogUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.synclog

import com.rochiee.classsync.domain.model.SyncLog
import com.rochiee.classsync.domain.repository.SyncLogRepository

class AddSyncLogUseCase(
    private val repository: SyncLogRepository
) {
    suspend operator fun invoke(log: SyncLog) {
        repository.addLog(log)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/synclog/ClearSyncLogsUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.synclog

import com.rochiee.classsync.domain.repository.SyncLogRepository

class ClearSyncLogsUseCase(
    private val repository: SyncLogRepository
) {
    suspend operator fun invoke() {
        repository.clearLogs()
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/worker/ScheduleBackgroundSyncUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.worker

import android.content.Context
import com.rochiee.classsync.domain.repository.SettingsRepository
import com.rochiee.classsync.worker.WorkScheduler
import kotlinx.coroutines.flow.first

class ScheduleBackgroundSyncUseCase(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        val settings = settingsRepository.observeSettings().first()
        if (!settings.backgroundSyncEnabled) {
            WorkScheduler.cancelAll(context)
            return
        }

        if (settings.gmailSyncEnabled) {
            WorkScheduler.scheduleGmailSync(context)
        }
        if (settings.classroomSyncEnabled) {
            WorkScheduler.scheduleClassroomSync(context)
        }
        if (settings.gmailSyncEnabled || settings.classroomSyncEnabled) {
            WorkScheduler.scheduleFullSync(context)
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/worker/CancelBackgroundSyncUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.worker

import android.content.Context
import com.rochiee.classsync.worker.WorkScheduler

class CancelBackgroundSyncUseCase(
    private val context: Context
) {
    operator fun invoke() {
        WorkScheduler.cancelAll(context)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/worker/RunOneTimeFullSyncUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.worker

import android.content.Context
import com.rochiee.classsync.worker.WorkScheduler

class RunOneTimeFullSyncUseCase(
    private val context: Context
) {
    operator fun invoke() {
        WorkScheduler.runOneTimeFullSync(context)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/settings/ObserveSettingsUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class ObserveSettingsUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.observeSettings()
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/settings/SetBackgroundSyncEnabledUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetBackgroundSyncEnabledUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setBackgroundSyncEnabled(enabled)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/settings/SetGmailSyncEnabledUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetGmailSyncEnabledUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setGmailSyncEnabled(enabled)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/settings/SetClassroomSyncEnabledUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetClassroomSyncEnabledUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setClassroomSyncEnabled(enabled)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/settings/SetNotificationParsingEnabledUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetNotificationParsingEnabledUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setNotificationParsingEnabled(enabled)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/settings/SetDefaultReminderHoursUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetDefaultReminderHoursUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(hours: Int) {
        repository.setDefaultReminderHours(hours)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/settings/SetLastSyncTimeUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.settings

import com.rochiee.classsync.domain.repository.SettingsRepository

class SetLastSyncTimeUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(timeMillis: Long) {
        repository.setLastSyncTimeMillis(timeMillis)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/export/ExportTasksCsvUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.export

import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.export.TaskExportManager
import java.io.File

class ExportTasksCsvUseCase(
    private val taskRepository: TaskRepository,
    private val taskExportManager: TaskExportManager
) {
    suspend operator fun invoke(): File {
        return taskExportManager.exportTasksCsv(taskRepository.getTasksSnapshot())
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/export/ExportTasksJsonUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.export

import com.rochiee.classsync.domain.repository.TaskRepository
import com.rochiee.classsync.export.TaskExportManager
import java.io.File

class ExportTasksJsonUseCase(
    private val taskRepository: TaskRepository,
    private val taskExportManager: TaskExportManager
) {
    suspend operator fun invoke(): File {
        return taskExportManager.exportTasksJson(taskRepository.getTasksSnapshot())
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/domain/usecase/notification/OpenNotificationAccessSettingsUseCase.kt

```kt
package com.rochiee.classsync.domain.usecase.notification

import android.content.Context
import com.rochiee.classsync.data.notification.NotificationPermissionHelper

class OpenNotificationAccessSettingsUseCase(
    private val context: Context
) {
    operator fun invoke() {
        NotificationPermissionHelper.openNotificationListenerSettings(context)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/taskengine/GmailTaskParser.kt

```kt
package com.rochiee.classsync.taskengine

import com.rochiee.classsync.data.remote.gmail.GmailMessageDto
import com.rochiee.classsync.domain.model.AcademicTask

object GmailTaskParser {
    private val keywords = listOf(
        "assignment", "due", "submit", "homework", "coursework",
        "quiz", "exam", "deadline", "posted", "upload", "turn in"
    )

    fun parse(message: GmailMessageDto): AcademicTask? {
        val subject = message.subject ?: ""
        val snippet = message.snippet ?: ""
        val body = message.body ?: ""
        val fullContent = "$subject $snippet $body"
        val normalizedContent = fullContent.lowercase()

        // Check if academic
        val isAcademic = keywords.any { normalizedContent.contains(it) }
        if (!isAcademic) return null

        // Ignore non-academic common emails
        val ignoreKeywords = listOf("login", "otp", "promotion", "spam", "social")
        if (ignoreKeywords.any { normalizedContent.contains(it) }) return null

        val title = subject.ifBlank { snippet.take(50) }
        val courseName = if (message.from?.contains("classroom.google.com") == true) {
            subject.split(":").firstOrNull()?.trim() ?: "Unknown Course"
        } else {
            "Academic"
        }
        val dueDate = DeadlineParser.parse(fullContent)

        return AcademicTask(
            title = title,
            description = snippet,
            courseName = courseName,
            isCompleted = false,
            dueDate = dueDate,
            priority = 2,
            source = "Gmail",
            sourceId = message.threadId.ifBlank { message.id },
            sourceLink = message.link,
            createdAtMillis = message.internalDateMillis,
            updatedAtMillis = message.internalDateMillis
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/taskengine/ClassroomTaskParser.kt

```kt
package com.rochiee.classsync.taskengine

import com.rochiee.classsync.data.remote.classroom.ClassroomCourseWorkDto
import com.rochiee.classsync.domain.model.AcademicTask

object ClassroomTaskParser {
    fun parse(courseWork: ClassroomCourseWorkDto, courseName: String): AcademicTask {
        return AcademicTask(
            title = courseWork.title,
            description = courseWork.description ?: "",
            courseName = courseName,
            isCompleted = courseWork.state == "TURNED_IN" || courseWork.state == "RETURNED",
            dueDate = courseWork.dueDateMillis,
            priority = 1, // Classroom tasks are high priority by default
            source = "Classroom",
            sourceId = "${courseWork.courseId}:${courseWork.id}",
            sourceLink = courseWork.alternateLink,
            createdAtMillis = courseWork.creationTimeMillis,
            updatedAtMillis = courseWork.updateTimeMillis
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/taskengine/NotificationTaskParser.kt

```kt
package com.rochiee.classsync.taskengine

import com.rochiee.classsync.domain.model.AcademicTask

object NotificationTaskParser {
    private val keywords = listOf(
        "assignment", "due", "submit", "submission", "coursework",
        "quiz", "exam", "homework", "deadline", "posted", "turn in", "upload"
    )

    fun parse(packageName: String, title: String, text: String): AcademicTask? {
        val fullContent = "$title $text".lowercase()
        val isAcademic = keywords.any { fullContent.contains(it) }

        if (!isAcademic) return null

        val courseName = if (packageName == "com.google.android.apps.classroom") {
            title.split(":").firstOrNull()?.trim() ?: "Unknown Course"
        } else {
            "Unknown Course"
        }
        val dueDate = DeadlineParser.parse("$title $text")

        return AcademicTask(
            title = title,
            description = text,
            courseName = courseName,
            isCompleted = false,
            dueDate = dueDate,
            priority = 1,
            source = "Notification",
            sourceId = TaskFingerprintGenerator.fingerprint(title, courseName, dueDate).let { "$packageName:$it" }
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/taskengine/TaskFingerprintGenerator.kt

```kt
package com.rochiee.classsync.taskengine

import java.text.Normalizer
import java.util.Locale

object TaskFingerprintGenerator {
    private val stopWords = setOf(
        "a", "an", "and", "assignment", "class", "coursework", "deadline", "due",
        "for", "homework", "new", "of", "on", "posted", "quiz", "submit", "submission",
        "task", "test", "the", "to", "turn", "upload"
    )

    fun normalizedTitle(title: String): String {
        return tokenize(title).joinToString(" ")
    }

    fun normalizedCourseName(courseName: String): String {
        return normalizeText(courseName)
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    fun fingerprint(title: String, courseName: String, dueDate: Long?): String {
        val titlePart = normalizedTitle(title)
        val coursePart = normalizedCourseName(courseName)
        val duePart = dueDate?.let { it / 3_600_000L } ?: "none"
        return "$titlePart|$coursePart|$duePart"
    }

    fun titleSimilarity(first: String, second: String): Double {
        val firstTokens = tokenize(first).toSet()
        val secondTokens = tokenize(second).toSet()
        if (firstTokens.isEmpty() || secondTokens.isEmpty()) return 0.0
        val intersection = firstTokens.intersect(secondTokens).size.toDouble()
        val union = firstTokens.union(secondTokens).size.toDouble()
        return if (union == 0.0) 0.0 else intersection / union
    }

    private fun tokenize(text: String): List<String> {
        return normalizeText(text)
            .split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() && it !in stopWords }
    }

    private fun normalizeText(text: String): String {
        val normalized = Normalizer.normalize(text.lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        return normalized
            .replace("\\p{M}+".toRegex(), "")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/taskengine/DuplicateTaskDetector.kt

```kt
package com.rochiee.classsync.taskengine

import com.rochiee.classsync.domain.model.AcademicTask
import kotlin.math.abs

object DuplicateTaskDetector {
    private const val DUE_DATE_WINDOW_MILLIS = 18L * 60L * 60L * 1000L

    fun findBestDuplicate(existingTasks: List<AcademicTask>, incomingTask: AcademicTask): AcademicTask? {
        return existingTasks
            .mapNotNull { existing ->
                val score = duplicateScore(existing, incomingTask)
                if (score > 0.0) existing to score else null
            }
            .maxByOrNull { it.second }
            ?.takeIf { it.second >= 0.72 }
            ?.first
    }

    fun merge(existingTask: AcademicTask, incomingTask: AcademicTask): AcademicTask {
        val existingPriority = sourcePriority(existingTask.source)
        val incomingPriority = sourcePriority(incomingTask.source)
        val preferred = if (incomingPriority >= existingPriority) incomingTask else existingTask
        val secondary = if (preferred === incomingTask) existingTask else incomingTask

        return preferred.copy(
            id = existingTask.id,
            title = chooseTitle(preferred, secondary),
            description = chooseDescription(preferred, secondary),
            courseName = chooseCourseName(preferred, secondary),
            isCompleted = existingTask.isCompleted || incomingTask.isCompleted,
            dueDate = chooseDueDate(preferred, secondary),
            priority = maxOf(existingTask.priority, incomingTask.priority),
            source = preferred.source,
            sourceId = preferred.sourceId ?: secondary.sourceId,
            sourceLink = preferred.sourceLink ?: secondary.sourceLink,
            createdAtMillis = minOf(existingTask.createdAtMillis, incomingTask.createdAtMillis),
            updatedAtMillis = maxOf(existingTask.updatedAtMillis, incomingTask.updatedAtMillis, System.currentTimeMillis())
        )
    }

    fun sourcePriority(source: String): Int {
        return when (source.lowercase()) {
            "classroom", "google classroom" -> 4
            "gmail" -> 3
            "notification" -> 2
            "manual", "raw text" -> 1
            else -> 1
        }
    }

    private fun duplicateScore(existingTask: AcademicTask, incomingTask: AcademicTask): Double {
        if (existingTask.sourceId != null &&
            incomingTask.sourceId != null &&
            existingTask.sourceId == incomingTask.sourceId
        ) {
            return 1.0
        }

        val titleSimilarity = TaskFingerprintGenerator.titleSimilarity(existingTask.title, incomingTask.title)
        val titlesMatch = titleSimilarity >= 0.72 ||
            TaskFingerprintGenerator.normalizedTitle(existingTask.title) ==
            TaskFingerprintGenerator.normalizedTitle(incomingTask.title)
        if (!titlesMatch) return 0.0

        val courseSimilarity = courseScore(existingTask.courseName, incomingTask.courseName)
        val dueDateSimilarity = dueDateScore(existingTask.dueDate, incomingTask.dueDate)

        return (titleSimilarity * 0.6) + (courseSimilarity * 0.2) + (dueDateSimilarity * 0.2)
    }

    private fun courseScore(first: String, second: String): Double {
        val normalizedFirst = TaskFingerprintGenerator.normalizedCourseName(first)
        val normalizedSecond = TaskFingerprintGenerator.normalizedCourseName(second)
        if (normalizedFirst.isBlank() || normalizedSecond.isBlank()) return 0.5
        if (normalizedFirst == normalizedSecond) return 1.0
        return TaskFingerprintGenerator.titleSimilarity(normalizedFirst, normalizedSecond)
    }

    private fun dueDateScore(first: Long?, second: Long?): Double {
        if (first == null || second == null) return 0.5
        val delta = abs(first - second)
        return if (delta <= DUE_DATE_WINDOW_MILLIS) 1.0 else 0.0
    }

    private fun chooseTitle(preferred: AcademicTask, secondary: AcademicTask): String {
        return if (preferred.title.length >= secondary.title.length) preferred.title else secondary.title
    }

    private fun chooseDescription(preferred: AcademicTask, secondary: AcademicTask): String {
        return if (preferred.description.length >= secondary.description.length) {
            preferred.description
        } else {
            secondary.description
        }
    }

    private fun chooseCourseName(preferred: AcademicTask, secondary: AcademicTask): String {
        return if (preferred.courseName.isNotBlank() && !preferred.courseName.equals("Unknown Course", ignoreCase = true)) {
            preferred.courseName
        } else {
            secondary.courseName
        }
    }

    private fun chooseDueDate(preferred: AcademicTask, secondary: AcademicTask): Long? {
        return preferred.dueDate ?: secondary.dueDate
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/taskengine/DeadlineParser.kt

```kt
package com.rochiee.classsync.taskengine

import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.abs

object DeadlineParser {
    private val monthLookup: Map<String, Int> by lazy {
        val monthNames = DateFormatSymbols(Locale.ENGLISH).months
        val shortMonthNames = DateFormatSymbols(Locale.ENGLISH).shortMonths
        buildMap {
            monthNames.forEachIndexed { index, value ->
                if (value.isNotBlank()) put(value.lowercase(Locale.ENGLISH), index)
            }
            shortMonthNames.forEachIndexed { index, value ->
                if (value.isNotBlank()) put(value.lowercase(Locale.ENGLISH).removeSuffix("."), index)
            }
        }
    }

    private val weekdayLookup = mapOf(
        "sunday" to Calendar.SUNDAY,
        "monday" to Calendar.MONDAY,
        "tuesday" to Calendar.TUESDAY,
        "wednesday" to Calendar.WEDNESDAY,
        "thursday" to Calendar.THURSDAY,
        "friday" to Calendar.FRIDAY,
        "saturday" to Calendar.SATURDAY
    )

    private val explicitMonthDayYear = Pattern.compile(
        "(?i)(?:due\\s*:?)?\\s*(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\\s+(\\d{1,2})(?:,\\s*(\\d{4}))?(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val explicitDayMonthYear = Pattern.compile(
        "(?i)(?:due\\s*:?)?\\s*(\\d{1,2})\\s+(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)(?:,\\s*(\\d{4}))?(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val todayTomorrowPattern = Pattern.compile(
        "(?i)(?:due\\s*)?(today|tomorrow)(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val weekdayPattern = Pattern.compile(
        "(?i)(?:due\\s*)?(next\\s+)?(monday|tuesday|wednesday|thursday|friday|saturday|sunday)(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val timeOnlyPattern = Pattern.compile(
        "(?i)(?:due\\s*)?(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2})"
    )

    fun parse(text: String, nowMillis: Long = System.currentTimeMillis()): Long? {
        val cleaned = text.replace("\n", " ").trim()
        if (cleaned.isBlank()) return null

        parseTodayTomorrow(cleaned, nowMillis)?.let { return it }
        parseMonthDay(cleaned, nowMillis)?.let { return it }
        parseDayMonth(cleaned, nowMillis)?.let { return it }
        parseWeekday(cleaned, nowMillis)?.let { return it }
        parseTimeOnly(cleaned, nowMillis)?.let { return it }

        return null
    }

    private fun parseTodayTomorrow(text: String, nowMillis: Long): Long? {
        val matcher = todayTomorrowPattern.matcher(text)
        if (!matcher.find()) return null

        val base = calendarAt(nowMillis)
        when (matcher.group(1)?.lowercase(Locale.ENGLISH)) {
            "today" -> Unit
            "tomorrow" -> base.add(Calendar.DAY_OF_YEAR, 1)
            else -> return null
        }
        applyTime(base, matcher.group(2))
        return base.timeInMillis
    }

    private fun parseMonthDay(text: String, nowMillis: Long): Long? {
        val matcher = explicitMonthDayYear.matcher(text)
        if (!matcher.find()) return null
        val month = monthLookup[matcher.group(1)?.lowercase(Locale.ENGLISH)?.removeSuffix(".")] ?: return null
        val day = matcher.group(2)?.toIntOrNull() ?: return null
        val year = matcher.group(3)?.toIntOrNull()
        return buildExplicitDate(nowMillis, year, month, day, matcher.group(4))
    }

    private fun parseDayMonth(text: String, nowMillis: Long): Long? {
        val matcher = explicitDayMonthYear.matcher(text)
        if (!matcher.find()) return null
        val day = matcher.group(1)?.toIntOrNull() ?: return null
        val month = monthLookup[matcher.group(2)?.lowercase(Locale.ENGLISH)?.removeSuffix(".")] ?: return null
        val year = matcher.group(3)?.toIntOrNull()
        return buildExplicitDate(nowMillis, year, month, day, matcher.group(4))
    }

    private fun parseWeekday(text: String, nowMillis: Long): Long? {
        val matcher = weekdayPattern.matcher(text)
        if (!matcher.find()) return null

        val isNext = !matcher.group(1).isNullOrBlank()
        val weekday = weekdayLookup[matcher.group(2)?.lowercase(Locale.ENGLISH)] ?: return null
        val calendar = calendarAt(nowMillis)
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        var delta = (weekday - currentDay + 7) % 7
        if (delta == 0 || isNext) {
            delta = if (delta == 0) 7 else delta + 7
        }
        calendar.add(Calendar.DAY_OF_YEAR, delta)
        applyTime(calendar, matcher.group(3))
        return calendar.timeInMillis
    }

    private fun parseTimeOnly(text: String, nowMillis: Long): Long? {
        val matcher = timeOnlyPattern.matcher(text)
        if (!matcher.find()) return null
        val calendar = calendarAt(nowMillis)
        applyTime(calendar, matcher.group(1))
        if (calendar.timeInMillis < nowMillis && abs(calendar.timeInMillis - nowMillis) > 60_000L) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun buildExplicitDate(
        nowMillis: Long,
        providedYear: Int?,
        month: Int,
        day: Int,
        timeText: String?
    ): Long? {
        val now = calendarAt(nowMillis)
        val candidate = calendarAt(nowMillis)
        candidate.set(Calendar.MONTH, month)
        candidate.set(Calendar.DAY_OF_MONTH, day)
        candidate.set(Calendar.YEAR, providedYear ?: now.get(Calendar.YEAR))
        applyTime(candidate, timeText)

        if (providedYear == null && candidate.timeInMillis < nowMillis) {
            candidate.add(Calendar.YEAR, 1)
        }
        return candidate.timeInMillis
    }

    private fun applyTime(calendar: Calendar, timeText: String?) {
        if (timeText.isNullOrBlank()) {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return
        }

        val normalized = timeText.trim().lowercase(Locale.ENGLISH)
        val matcher = Regex("(\\d{1,2}):(\\d{2})(?:\\s*(am|pm))?").matchEntire(normalized) ?: run {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return
        }

        var hour = matcher.groupValues[1].toInt()
        val minute = matcher.groupValues[2].toInt()
        val meridiem = matcher.groupValues.getOrNull(3)

        if (meridiem == "pm" && hour < 12) hour += 12
        if (meridiem == "am" && hour == 12) hour = 0

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun calendarAt(timeMillis: Long): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/worker/GmailSyncWorker.kt

```kt
package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.domain.model.SyncLog

class GmailSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as ClassSyncApplication
        val googleAuthManager = app.container.googleAuthManager
        val syncLogRepository = app.container.syncLogRepository
        val syncGmailTasksUseCase = app.container.syncGmailTasksUseCase

        if (!googleAuthManager.isSignedIn()) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL_WORKER",
                    status = "SKIPPED",
                    message = "Skipped Gmail sync because the user is not signed in.",
                    timestamp = System.currentTimeMillis()
                )
            )
            return Result.success()
        }

        return try {
            syncGmailTasksUseCase()
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL_WORKER",
                    status = "SUCCESS",
                    message = "Background Gmail sync completed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success()
        } catch (e: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "GMAIL_WORKER",
                    status = "ERROR",
                    message = e.message ?: "Background Gmail sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/worker/ClassroomSyncWorker.kt

```kt
package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.domain.model.SyncLog

class ClassroomSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as ClassSyncApplication
        val googleAuthManager = app.container.googleAuthManager
        val syncLogRepository = app.container.syncLogRepository
        val syncClassroomCourseworkUseCase = app.container.syncClassroomCourseworkUseCase

        if (!googleAuthManager.isSignedIn()) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_WORKER",
                    status = "SKIPPED",
                    message = "Skipped Classroom sync because the user is not signed in.",
                    timestamp = System.currentTimeMillis()
                )
            )
            return Result.success()
        }

        return try {
            syncClassroomCourseworkUseCase()
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_WORKER",
                    status = "SUCCESS",
                    message = "Background Classroom sync completed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success()
        } catch (e: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "CLASSROOM_WORKER",
                    status = "ERROR",
                    message = e.message ?: "Background Classroom sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/worker/FullSyncWorker.kt

```kt
package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rochiee.classsync.ClassSyncApplication
import com.rochiee.classsync.domain.model.SyncLog
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FullSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        val app = applicationContext as ClassSyncApplication
        val googleAuthManager = app.container.googleAuthManager
        val syncLogRepository = app.container.syncLogRepository
        val syncGmailTasksUseCase = app.container.syncGmailTasksUseCase
        val syncClassroomCourseworkUseCase = app.container.syncClassroomCourseworkUseCase

        if (!googleAuthManager.isSignedIn()) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "FULL_SYNC_WORKER",
                    status = "SKIPPED",
                    message = "Skipped full sync because the user is not signed in.",
                    timestamp = System.currentTimeMillis()
                )
            )
            return@coroutineScope Result.success()
        }

        try {
            val gmailJob = async { syncGmailTasksUseCase() }
            val classroomJob = async { syncClassroomCourseworkUseCase() }

            gmailJob.await()
            classroomJob.await()

            syncLogRepository.addLog(
                SyncLog(
                    source = "FULL_SYNC_WORKER",
                    status = "SUCCESS",
                    message = "Background full sync completed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            Result.success()
        } catch (e: Exception) {
            syncLogRepository.addLog(
                SyncLog(
                    source = "FULL_SYNC_WORKER",
                    status = "ERROR",
                    message = e.message ?: "Background full sync failed.",
                    timestamp = System.currentTimeMillis()
                )
            )
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/worker/WorkScheduler.kt

```kt
package com.rochiee.classsync.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkScheduler {
    const val GMAIL_SYNC_WORK = "GMAIL_SYNC_WORK"
    const val CLASSROOM_SYNC_WORK = "CLASSROOM_SYNC_WORK"
    const val FULL_SYNC_WORK = "FULL_SYNC_WORK"

    fun scheduleGmailSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<GmailSyncWorker>(3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            GMAIL_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleClassroomSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<ClassroomSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            CLASSROOM_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleFullSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<FullSyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            FULL_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleAll(context: Context) {
        scheduleGmailSync(context)
        scheduleClassroomSync(context)
        scheduleFullSync(context)
    }

    fun cancelAll(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(GMAIL_SYNC_WORK)
        workManager.cancelUniqueWork(CLASSROOM_SYNC_WORK)
        workManager.cancelUniqueWork(FULL_SYNC_WORK)
    }

    fun runOneTimeFullSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<FullSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/reminder/ReminderNotificationHelper.kt

```kt
package com.rochiee.classsync.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rochiee.classsync.R

object ReminderNotificationHelper {
    const val CHANNEL_ID = "task_reminders"
    private const val CHANNEL_NAME = "Task Reminders"
    private const val CHANNEL_DESCRIPTION = "Reminders for upcoming academic tasks"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    fun showReminderNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        ensureChannel(context)
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/reminder/ReminderScheduler.kt

```kt
package com.rochiee.classsync.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class ReminderScheduler(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val alarmManager: AlarmManager? by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    suspend fun schedule(task: AcademicTask) {
        if (task.id <= 0) return
        if (task.isCompleted || task.dueDate == null) {
            cancel(task)
            return
        }

        val settings = settingsRepository.observeSettings().first()
        if (!settings.notificationParsingEnabled) {
            cancel(task)
            return
        }
        val reminderAtMillis = task.dueDate - (settings.defaultReminderHours * 60L * 60L * 1000L)
        if (reminderAtMillis <= System.currentTimeMillis()) {
            cancel(task)
            return
        }

        val pendingIntent = createPendingIntent(task)
        alarmManager?.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderAtMillis,
            pendingIntent
        )
    }

    fun cancel(task: AcademicTask) {
        if (task.id <= 0) return
        val pendingIntent = createPendingIntent(task)
        alarmManager?.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun createPendingIntent(task: AcademicTask): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, task.id)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(TaskReminderReceiver.EXTRA_TASK_COURSE, task.courseName)
        }

        return PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/reminder/TaskReminderReceiver.kt

```kt
package com.rochiee.classsync.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, 0)
        val title = intent.getStringExtra(EXTRA_TASK_TITLE).orEmpty()
        val courseName = intent.getStringExtra(EXTRA_TASK_COURSE).orEmpty()

        val body = if (courseName.isBlank()) {
            "Task reminder: $title"
        } else {
            "Reminder for $courseName: $title"
        }

        ReminderNotificationHelper.showReminderNotification(
            context = context,
            notificationId = taskId,
            title = title.ifBlank { "Task Reminder" },
            message = body
        )
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_COURSE = "extra_task_course"
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/export/TaskCsvExporter.kt

```kt
package com.rochiee.classsync.export

import com.rochiee.classsync.domain.model.AcademicTask

class TaskCsvExporter {
    fun export(tasks: List<AcademicTask>): String {
        val header = "id,title,description,courseName,isCompleted,dueDate,priority,source,sourceId,sourceLink,createdAtMillis,updatedAtMillis"
        val rows = tasks.map { task ->
            listOf(
                task.id.toString(),
                escape(task.title),
                escape(task.description),
                escape(task.courseName),
                task.isCompleted.toString(),
                task.dueDate?.toString().orEmpty(),
                task.priority.toString(),
                escape(task.source),
                escape(task.sourceId.orEmpty()),
                escape(task.sourceLink.orEmpty()),
                task.createdAtMillis.toString(),
                task.updatedAtMillis.toString()
            ).joinToString(",")
        }
        return buildString {
            appendLine(header)
            rows.forEach { appendLine(it) }
        }
    }

    private fun escape(value: String): String {
        return "\"${value.replace("\"", "\"\"")}\""
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/export/TaskJsonExporter.kt

```kt
package com.rochiee.classsync.export

import com.rochiee.classsync.domain.model.AcademicTask
import org.json.JSONArray
import org.json.JSONObject

class TaskJsonExporter {
    fun export(tasks: List<AcademicTask>): String {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(
                JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("description", task.description)
                    put("courseName", task.courseName)
                    put("isCompleted", task.isCompleted)
                    put("dueDate", task.dueDate)
                    put("priority", task.priority)
                    put("source", task.source)
                    put("sourceId", task.sourceId)
                    put("sourceLink", task.sourceLink)
                    put("createdAtMillis", task.createdAtMillis)
                    put("updatedAtMillis", task.updatedAtMillis)
                }
            )
        }
        return array.toString(2)
    }
}

```

## /Users/rohanc/AndroidStudioProjects/classsync/app/src/main/java/com/rochiee/classsync/export/TaskExportManager.kt

```kt
package com.rochiee.classsync.export

import android.content.Context
import android.os.Environment
import com.rochiee.classsync.domain.model.AcademicTask
import java.io.File

class TaskExportManager(
    private val context: Context,
    private val csvExporter: TaskCsvExporter,
    private val jsonExporter: TaskJsonExporter
) {
    fun exportTasksCsv(tasks: List<AcademicTask>): File {
        return writeExportFile(
            fileName = "classsync_tasks_${System.currentTimeMillis()}.csv",
            content = csvExporter.export(tasks)
        )
    }

    fun exportTasksJson(tasks: List<AcademicTask>): File {
        return writeExportFile(
            fileName = "classsync_tasks_${System.currentTimeMillis()}.json",
            content = jsonExporter.export(tasks)
        )
    }

    private fun writeExportFile(fileName: String, content: String): File {
        val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: File(context.filesDir, "exports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val file = File(exportDir, fileName)
        file.writeText(content)
        return file
    }
}

```

