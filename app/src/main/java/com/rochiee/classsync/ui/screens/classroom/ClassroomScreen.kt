package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.classroom.ClassroomScreenEvent
import com.rochiee.classsync.bloc.classroom.ClassroomScreenState
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun ClassroomScreen(
    classroomState: ClassroomScreenState,
    onClassroomEvent: (ClassroomScreenEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var selectedTab by remember { mutableStateOf(CourseTab.Overview) }
    val selectedSummary = classroomState.courseSummaries.firstOrNull { it.courseId == classroomState.selectedCourseId }

    Column(
        modifier = Modifier
            .padding(spacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        CourseListScreen(
            summaries = classroomState.courseSummaries,
            isRefreshing = classroomState.isRefreshing,
            onRefresh = { onClassroomEvent(ClassroomScreenEvent.RefreshCourses) },
            onSelectCourse = { courseId -> onClassroomEvent(ClassroomScreenEvent.SelectCourse(courseId)) }
        )

        selectedSummary?.let { summary ->
            CourseDetailScreen(
                summary = summary,
                selectedTab = selectedTab,
                onSelectTab = { selectedTab = it },
                selectedTasks = classroomState.selectedCourseTasks,
                selectedEvents = classroomState.selectedCourseEvents
            )
        }
    }
}
