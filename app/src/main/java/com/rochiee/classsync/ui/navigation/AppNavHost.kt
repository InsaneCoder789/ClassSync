package com.rochiee.classsync.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rochiee.classsync.BuildConfig
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
import com.rochiee.classsync.ui.screens.debug.DebugScreen
import com.rochiee.classsync.ui.screens.exam.ExamModeScreen
import com.rochiee.classsync.ui.screens.home.HomeScreen
import com.rochiee.classsync.ui.screens.onboarding.OnboardingScreen
import com.rochiee.classsync.ui.screens.planner.PlannerScreen
import com.rochiee.classsync.ui.screens.settings.PrivacyPolicyScreen
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
    onBeginGoogleSignIn: (Context) -> Intent?,
    onCompleteGoogleSignIn: (Intent?) -> Unit,
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
    val initialRoute = remember(settingsState.isLoading, settingsState.onboardingCompleted, requestedStartRoute) {
        when {
            settingsState.isLoading -> null
            settingsState.onboardingCompleted -> requestedStartRoute ?: AppDestination.Home.route
            else -> AppDestination.Onboarding.route
        }
    }
    val showBottomBar = remember(backStackEntry) {
        AppDestination.bottomBarDestinations.any { it.route == backStackEntry?.destination?.route }
    }
    val showTopBar = currentRoute != null && currentRoute != AppDestination.Onboarding.route
    val isHomeRoute = currentRoute == AppDestination.Home.route

    LaunchedEffect(settingsState.isLoading, settingsState.onboardingCompleted, requestedStartRoute) {
        if (settingsState.isLoading) return@LaunchedEffect
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                        scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.98f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    title = {
                        if (isHomeRoute) {
                            AppLogoLockup(subtitle = "Academic control deck")
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = titleForRoute(currentRoute).uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = titleForRoute(currentRoute), style = MaterialTheme.typography.titleLarge)
                            }
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
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppDestination.bottomBarDestinations.forEach { destination ->
                            val selected = backStackEntry?.destination?.hierarchy?.any { it.route == destination.route } == true
                            GlassNavItem(
                                label = destination.label,
                                icon = iconForDestination(destination),
                                selected = selected,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(AppDestination.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (initialRoute == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = initialRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppDestination.Onboarding.route) {
                    OnboardingScreen(
                        authState = authState,
                        settingsState = settingsState,
                        syncState = syncState,
                        onBeginGoogleSignIn = onBeginGoogleSignIn,
                        onCompleteGoogleSignIn = onCompleteGoogleSignIn,
                        onAuthEvent = onAuthEvent,
                        onSyncEvent = onSyncEvent,
                        onSettingsEvent = onSettingsEvent,
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
                        onAuthEvent = onAuthEvent,
                        onSettingsEvent = onSettingsEvent,
                        onSyncEvent = onSyncEvent,
                        onNavigateToAuth = { navController.navigate(AppDestination.Auth.route) },
                        onNavigateToPrivacyPolicy = { navController.navigate(AppDestination.PrivacyPolicy.route) }
                    )
                }
                composable(AppDestination.PrivacyPolicy.route) {
                    PrivacyPolicyScreen()
                }
                if (BuildConfig.DEBUG) {
                    composable(AppDestination.Debug.route) {
                        DebugScreen(
                            authState = authState,
                            taskState = taskState,
                            syncState = syncState,
                            settingsState = settingsState,
                            eventState = eventState,
                            plannerState = plannerState,
                            onBeginGoogleSignIn = onBeginGoogleSignIn,
                            onCompleteGoogleSignIn = onCompleteGoogleSignIn,
                            onAuthEvent = onAuthEvent,
                            onTaskEvent = onTaskEvent,
                            onSyncEvent = onSyncEvent,
                            onSettingsEvent = onSettingsEvent,
                            onEventEvent = onEventEvent,
                            onPlannerEvent = onPlannerEvent
                        )
                    }
                }
                composable(AppDestination.Auth.route) {
                    AuthScreen(
                        authState = authState,
                        syncState = syncState,
                        onBeginGoogleSignIn = onBeginGoogleSignIn,
                        onCompleteGoogleSignIn = onCompleteGoogleSignIn,
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
}

@Composable
private fun GlassNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        } else {
            Color.Transparent
        },
        shadowElevation = if (selected) 4.dp else 0.dp,
        tonalElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            } else {
                Color.Transparent
            }
        )
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 68.dp)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun iconForDestination(destination: AppDestination): ImageVector {
    return when (destination) {
        AppDestination.Home -> Icons.Rounded.Home
        AppDestination.Tasks -> Icons.Rounded.AssignmentTurnedIn
        AppDestination.Planner -> Icons.Rounded.CalendarMonth
        AppDestination.Settings -> Icons.Rounded.Settings
        else -> Icons.Rounded.Home
    }
}

private fun titleForRoute(route: String?): String {
    val destination = when {
        route == null -> AppDestination.Home
        route == AppDestination.Home.route -> AppDestination.Home
        route == AppDestination.Tasks.route -> AppDestination.Tasks
        route == AppDestination.Planner.route -> AppDestination.Planner
        route == AppDestination.Settings.route -> AppDestination.Settings
        route == AppDestination.Debug.route -> AppDestination.Debug
        route == AppDestination.Auth.route -> AppDestination.Auth
        route == AppDestination.Activity.route -> AppDestination.Activity
        route.startsWith(AppDestination.EventDetail.route) -> AppDestination.EventDetail
        route == AppDestination.StudyPlanner.route -> AppDestination.StudyPlanner
        route == AppDestination.ExamMode.route -> AppDestination.ExamMode
        route == AppDestination.PrivacyPolicy.route -> AppDestination.PrivacyPolicy
        else -> AppDestination.Home
    }
    return destination.label
}
