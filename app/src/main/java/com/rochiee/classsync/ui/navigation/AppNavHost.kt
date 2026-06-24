package com.rochiee.classsync.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rochiee.classsync.bloc.auth.AuthEvent
import com.rochiee.classsync.bloc.auth.AuthUiState
import com.rochiee.classsync.bloc.classroom.ClassroomScreenEvent
import com.rochiee.classsync.bloc.classroom.ClassroomScreenState
import com.rochiee.classsync.bloc.eventdetail.EventDetailEvent
import com.rochiee.classsync.bloc.eventdetail.EventDetailState
import com.rochiee.classsync.bloc.exam.ExamModeScreenEvent
import com.rochiee.classsync.bloc.exam.ExamModeScreenState
import com.rochiee.classsync.bloc.event.EventEvent
import com.rochiee.classsync.bloc.event.EventState
import com.rochiee.classsync.bloc.planner.PlannerEvent
import com.rochiee.classsync.bloc.planner.PlannerState
import com.rochiee.classsync.bloc.settings.SettingsEvent
import com.rochiee.classsync.bloc.settings.SettingsState
import com.rochiee.classsync.bloc.study.StudyPlanEvent
import com.rochiee.classsync.bloc.study.StudyPlanState
import com.rochiee.classsync.bloc.sync.SyncEvent
import com.rochiee.classsync.bloc.sync.SyncState
import com.rochiee.classsync.bloc.task.TaskEvent
import com.rochiee.classsync.bloc.task.TaskState
import com.rochiee.classsync.ui.components.AppLogoLockup
import com.rochiee.classsync.ui.screens.activity.ActivityScreen
import com.rochiee.classsync.ui.screens.activity.EventDetailScreen
import com.rochiee.classsync.ui.screens.auth.AuthScreen
import com.rochiee.classsync.ui.screens.classroom.ClassroomScreen
import com.rochiee.classsync.ui.screens.debug.DebugScreen
import com.rochiee.classsync.ui.screens.exam.ExamModeScreen
import com.rochiee.classsync.ui.screens.home.HomeScreen
import com.rochiee.classsync.ui.screens.onboarding.OnboardingScreen
import com.rochiee.classsync.ui.screens.planner.PlannerScreen
import com.rochiee.classsync.ui.screens.settings.SettingsScreen
import com.rochiee.classsync.ui.screens.study.StudyPlannerScreen
import com.rochiee.classsync.ui.screens.tasks.TasksScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    requestedStartRoute: String? = null,
    authState: AuthUiState,
    settingsState: SettingsState,
    taskState: TaskState,
    syncState: SyncState,
    eventState: EventState,
    eventDetailState: EventDetailState,
    plannerState: PlannerState,
    classroomState: ClassroomScreenState,
    studyPlanState: StudyPlanState,
    examModeState: ExamModeScreenState,
    onAuthEvent: (AuthEvent) -> Unit,
    onTaskEvent: (TaskEvent) -> Unit,
    onSyncEvent: (SyncEvent) -> Unit,
    onSettingsEvent: (SettingsEvent) -> Unit,
    onEventEvent: (EventEvent) -> Unit,
    onEventDetailEvent: (EventDetailEvent) -> Unit,
    onPlannerEvent: (PlannerEvent) -> Unit,
    onClassroomEvent: (ClassroomScreenEvent) -> Unit,
    onStudyPlanEvent: (StudyPlanEvent) -> Unit,
    onExamModeEvent: (ExamModeScreenEvent) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute?.let { route ->
        AppDestination.bottomBarDestinations.any { destination -> destination.route == route }
    } == true
    val showTopBar = currentRoute != AppDestination.Onboarding.route
    val isHomeRoute = currentRoute == AppDestination.Home.route

    LaunchedEffect(settingsState.onboardingCompleted) {
        val target = if (settingsState.onboardingCompleted) {
            requestedStartRoute ?: AppDestination.Home.route
        } else {
            AppDestination.Onboarding.route
        }
        if (backStackEntry?.destination?.route != target) {
            navController.navigate(target) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        if (isHomeRoute) {
                            AppLogoLockup(subtitle = "Everything in sync")
                        } else {
                            Text(text = titleForRoute(currentRoute))
                        }
                    },
                    navigationIcon = if (isHomeRoute) {
                        {}
                    } else {
                        {
                            IconButton(
                                onClick = {
                                    when {
                                        navController.previousBackStackEntry != null -> navController.popBackStack()
                                        currentRoute != AppDestination.Home.route -> {
                                            navController.navigate(AppDestination.Home.route) {
                                                popUpTo(navController.graph.startDestinationId)
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = com.rochiee.classsync.R.drawable.ic_back),
                                    contentDescription = "Back",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)) {
                    AppDestination.bottomBarDestinations.forEach { destination ->
                        val selected = backStackEntry?.destination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(AppDestination.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            ),
                            icon = {
                                destination.iconRes?.let {
                                    Image(
                                        painter = painterResource(id = it),
                                        contentDescription = destination.label,
                                        modifier = Modifier.size(28.dp)
                                    )
                                } ?: Text(destination.label.take(1))
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.Onboarding.route) {
                OnboardingScreen(
                    authState = authState,
                    settingsState = settingsState,
                    syncState = syncState,
                    onAuthEvent = onAuthEvent,
                    onSyncEvent = onSyncEvent,
                    onSettingsEvent = onSettingsEvent,
                    onOpenNotificationAccess = { onTaskEvent(TaskEvent.OpenNotificationAccessSettings) },
                    onRequestReminderPermissionExplained = {
                        onSettingsEvent(SettingsEvent.SetNotificationPermissionExplained(true))
                    },
                    onComplete = {
                        onSettingsEvent(SettingsEvent.SetOnboardingCompleted(true))
                    }
                )
            }
            composable(AppDestination.Home.route) {
                HomeScreen(
                    authState = authState,
                    taskState = taskState,
                    syncState = syncState,
                    eventState = eventState,
                    onNavigateToActivity = { navController.navigate(AppDestination.Activity.route) },
                    onNavigateToStudyPlanner = { navController.navigate(AppDestination.StudyPlanner.route) },
                    onNavigateToExamMode = { navController.navigate(AppDestination.ExamMode.route) },
                    onNavigateToDebug = { navController.navigate(AppDestination.Debug.route) },
                    onNavigateToAuth = { navController.navigate(AppDestination.Auth.route) }
                )
            }
            composable(AppDestination.Tasks.route) {
                TasksScreen(
                    taskState = taskState,
                    syncState = syncState,
                    onTaskEvent = onTaskEvent,
                    onSyncEvent = onSyncEvent
                )
            }
            composable(AppDestination.Classroom.route) {
                ClassroomScreen(
                    classroomState = classroomState,
                    onClassroomEvent = onClassroomEvent
                )
            }
            composable(AppDestination.Planner.route) {
                PlannerScreen(
                    plannerState = plannerState,
                    classroomState = classroomState,
                    onPlannerEvent = onPlannerEvent
                )
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(
                    settingsState = settingsState,
                    authState = authState,
                    syncState = syncState,
                    onSettingsEvent = onSettingsEvent,
                    onSyncEvent = onSyncEvent,
                    onNavigateToDebug = { navController.navigate(AppDestination.Debug.route) },
                    onNavigateToAuth = { navController.navigate(AppDestination.Auth.route) }
                )
            }
            composable(AppDestination.Debug.route) {
                DebugScreen(
                    authState = authState,
                    taskState = taskState,
                    syncState = syncState,
                    settingsState = settingsState,
                    eventState = eventState,
                    plannerState = plannerState,
                    onAuthEvent = onAuthEvent,
                    onTaskEvent = onTaskEvent,
                    onSyncEvent = onSyncEvent,
                    onSettingsEvent = onSettingsEvent,
                    onEventEvent = onEventEvent,
                    onPlannerEvent = onPlannerEvent
                )
            }
            composable(AppDestination.Auth.route) {
                AuthScreen(
                    authState = authState,
                    syncState = syncState,
                    onAuthEvent = onAuthEvent,
                    onSyncEvent = onSyncEvent
                )
            }
            composable(AppDestination.Activity.route) {
                ActivityScreen(
                    eventState = eventState,
                    onOpenEvent = { eventId -> navController.navigate("${AppDestination.EventDetail.route}/$eventId") }
                )
            }
            composable("${AppDestination.EventDetail.route}/{eventId}") { backStack ->
                val eventId = backStack.arguments?.getString("eventId").orEmpty()
                EventDetailScreen(
                    eventId = eventId,
                    state = eventDetailState,
                    onEvent = onEventDetailEvent
                )
            }
            composable(AppDestination.StudyPlanner.route) {
                StudyPlannerScreen(
                    state = studyPlanState,
                    onEvent = onStudyPlanEvent
                )
            }
            composable(AppDestination.ExamMode.route) {
                ExamModeScreen(
                    state = examModeState,
                    onEvent = onExamModeEvent,
                    onOpenStudyPlanner = { navController.navigate(AppDestination.StudyPlanner.route) }
                )
            }
        }
    }
}

private fun titleForRoute(route: String?): String {
    val destination = when {
        route == null -> AppDestination.Home
        route == AppDestination.Home.route -> AppDestination.Home
        route == AppDestination.Tasks.route -> AppDestination.Tasks
        route == AppDestination.Classroom.route -> AppDestination.Classroom
        route == AppDestination.Planner.route -> AppDestination.Planner
        route == AppDestination.Settings.route -> AppDestination.Settings
        route == AppDestination.Debug.route -> AppDestination.Debug
        route == AppDestination.Auth.route -> AppDestination.Auth
        route == AppDestination.Activity.route -> AppDestination.Activity
        route.startsWith(AppDestination.EventDetail.route) -> AppDestination.EventDetail
        route == AppDestination.StudyPlanner.route -> AppDestination.StudyPlanner
        route == AppDestination.ExamMode.route -> AppDestination.ExamMode
        else -> AppDestination.Home
    }
    return destination.label
}
