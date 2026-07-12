package com.rochiee.classsync

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rochiee.classsync.domain.model.ThemeMode
import com.rochiee.classsync.bloc.auth.AuthBlocViewModel
import com.rochiee.classsync.bloc.auth.AuthEvent
import com.rochiee.classsync.bloc.classroom.ClassroomScreenViewModel
import com.rochiee.classsync.bloc.eventdetail.EventDetailViewModel
import com.rochiee.classsync.bloc.exam.ExamModeBlocViewModel
import com.rochiee.classsync.bloc.event.EventBlocViewModel
import com.rochiee.classsync.bloc.planner.PlannerBlocViewModel
import com.rochiee.classsync.bloc.settings.SettingsBlocViewModel
import com.rochiee.classsync.bloc.study.StudyPlanBlocViewModel
import com.rochiee.classsync.bloc.sync.SyncBlocViewModel
import com.rochiee.classsync.bloc.task.TaskBlocViewModel
import com.rochiee.classsync.di.ViewModelFactory
import com.rochiee.classsync.ui.navigation.AppNavHost
import com.rochiee.classsync.ui.screens.startup.ReturnWelcomeScreen
import com.rochiee.classsync.ui.theme.ClassSyncTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_START_DESTINATION = "classsync.extra.START_DESTINATION"
        private const val APP_SPLASH_DURATION_MILLIS = 650L
        private const val OPEN_REFRESH_INTERVAL_MILLIS = 12L * 60L * 60L * 1000L
    }

    private val taskViewModel: TaskBlocViewModel by viewModels {
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
    private val classroomViewModel: ClassroomScreenViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }
    private val eventDetailViewModel: EventDetailViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }
    private val studyPlanViewModel: StudyPlanBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }
    private val examModeViewModel: ExamModeBlocViewModel by viewModels {
        ViewModelFactory((application as ClassSyncApplication).container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            settingsViewModel.state.value.isLoading || authViewModel.state.value.isLoading
        }
        enableEdgeToEdge()
        authViewModel.onEvent(AuthEvent.CheckAuthState)

        setContent {
            val taskState by taskViewModel.state.collectAsState()
            val authState by authViewModel.state.collectAsState()
            val syncState by syncViewModel.state.collectAsState()
            val settingsState by settingsViewModel.state.collectAsState()
            val eventState by eventViewModel.state.collectAsState()
            val plannerState by plannerViewModel.state.collectAsState()
            val classroomState by classroomViewModel.state.collectAsState()
            val eventDetailState by eventDetailViewModel.state.collectAsState()
            val studyPlanState by studyPlanViewModel.state.collectAsState()
            val examModeState by examModeViewModel.state.collectAsState()
            val followsSystemDark = isSystemInDarkTheme()
            val useDarkTheme = when (settingsState.themeMode) {
                ThemeMode.SYSTEM -> followsSystemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            ClassSyncTheme(darkTheme = useDarkTheme) {
                var showLaunchSplash by remember {
                    mutableStateOf(true)
                }

                LaunchedEffect(settingsState.isLoading) {
                    if (settingsState.isLoading) return@LaunchedEffect
                    delay(APP_SPLASH_DURATION_MILLIS)
                    showLaunchSplash = false
                    settingsViewModel.onEvent(
                        com.rochiee.classsync.bloc.settings.SettingsEvent.SetLastAppOpenTime(
                            System.currentTimeMillis()
                        )
                    )
                }

                LaunchedEffect(
                    authState.isSignedIn,
                    settingsState.isLoading,
                    settingsState.backgroundSyncEnabled,
                    settingsState.lastSyncTimeMillis,
                    showLaunchSplash
                ) {
                    val shouldAutoRefresh = settingsState.lastSyncTimeMillis == null ||
                        System.currentTimeMillis() - (settingsState.lastSyncTimeMillis ?: 0L) >= OPEN_REFRESH_INTERVAL_MILLIS
                    if (
                        !settingsState.isLoading &&
                        !showLaunchSplash &&
                        settingsState.backgroundSyncEnabled &&
                        authState.isSignedIn &&
                        shouldAutoRefresh
                    ) {
                        syncViewModel.onEvent(com.rochiee.classsync.bloc.sync.SyncEvent.RunAutoRefreshOnOpen)
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    val sanitizedStartRoute = remember(intent) {
                        com.rochiee.classsync.ui.navigation.AppDestination.sanitizeExternalStartRoute(
                            route = intent?.getStringExtra(EXTRA_START_DESTINATION),
                            allowDebugTools = BuildConfig.DEBUG
                        )
                    }
                    AppNavHost(
                        requestedStartRoute = sanitizedStartRoute,
                        authState = authState,
                        settingsState = settingsState,
                        taskState = taskState,
                        syncState = syncState,
                        eventState = eventState,
                        eventDetailState = eventDetailState,
                        plannerState = plannerState,
                        classroomState = classroomState,
                        studyPlanState = studyPlanState,
                        examModeState = examModeState,
                        onBeginGoogleSignIn = authViewModel::beginSignInIntent,
                        onCompleteGoogleSignIn = authViewModel::completeSignIn,
                        onAuthEvent = authViewModel::onEvent,
                        onTaskEvent = taskViewModel::onEvent,
                        onSyncEvent = syncViewModel::onEvent,
                        onSettingsEvent = settingsViewModel::onEvent,
                        onEventEvent = eventViewModel::onEvent,
                        onEventDetailEvent = eventDetailViewModel::onEvent,
                        onPlannerEvent = plannerViewModel::onEvent,
                        onClassroomEvent = classroomViewModel::onEvent,
                        onStudyPlanEvent = studyPlanViewModel::onEvent,
                        onExamModeEvent = examModeViewModel::onEvent
                    )
                    if (showLaunchSplash) {
                        ReturnWelcomeScreen(
                            darkTheme = useDarkTheme,
                            durationMillis = APP_SPLASH_DURATION_MILLIS.toInt(),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
