package com.rochiee.classsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
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
import com.rochiee.classsync.ui.theme.ClassSyncTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_START_DESTINATION = "classsync.extra.START_DESTINATION"
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
        super.onCreate(savedInstanceState)
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

            ClassSyncTheme(darkTheme = settingsState.themeMode == ThemeMode.DARK) {
                AppNavHost(
                    requestedStartRoute = intent?.getStringExtra(EXTRA_START_DESTINATION),
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
            }
        }
    }
}
