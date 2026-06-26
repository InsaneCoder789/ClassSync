package com.rochiee.classsync.ui.navigation

import com.rochiee.classsync.R

sealed class AppDestination(
    val route: String,
    val label: String,
    val inBottomBar: Boolean = false,
    val iconRes: Int? = null
) {
    data object Onboarding : AppDestination("onboarding", "Start")
    data object Home : AppDestination("home", "Home", true, R.drawable.nav_home)
    data object Tasks : AppDestination("tasks", "Tasks", true, R.drawable.nav_tasks)
    data object Classroom : AppDestination("classroom", "Classes", false, R.drawable.nav_classroom)
    data object Planner : AppDestination("planner", "Planner", true, R.drawable.nav_planner)
    data object Settings : AppDestination("settings", "Settings", true, R.drawable.nav_settings)
    data object Debug : AppDestination("debug", "Debug")
    data object Auth : AppDestination("auth", "Auth")
    data object Activity : AppDestination("activity", "Activity")
    data object EventDetail : AppDestination("eventDetail", "Event Detail")
    data object StudyPlanner : AppDestination("studyPlanner", "Study Planner")
    data object ExamMode : AppDestination("examMode", "Exam Mode")

    companion object {
        val bottomBarDestinations = listOf(Home, Tasks, Planner, Settings)
    }
}
