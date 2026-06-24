package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rochiee.classsync.planner.PlannerFilter
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun PlannerFilterSheet(
    current: PlannerFilter,
    availableCourseIds: List<Pair<String, String>>,
    onApply: (PlannerFilter) -> Unit
) {
    val spacing = LocalSpacing.current
    var showTasks by remember(current) { mutableStateOf(current.showTasks) }
    var showAssignments by remember(current) { mutableStateOf(current.showAssignments) }
    var showQuizzes by remember(current) { mutableStateOf(current.showQuizzes) }
    var showExams by remember(current) { mutableStateOf(current.showExams) }
    var showAnnouncements by remember(current) { mutableStateOf(current.showAnnouncements) }
    var showMaterials by remember(current) { mutableStateOf(current.showMaterials) }
    var showCompleted by remember(current) { mutableStateOf(current.showCompleted) }
    var selectedCourseId by remember(current) { mutableStateOf(current.courseId) }
    TintedPanel {
        Text(text = "Planner filters", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            PlannerToggleRow("Tasks", showTasks) { showTasks = it }
            PlannerToggleRow("Assignments", showAssignments) { showAssignments = it }
            PlannerToggleRow("Quizzes", showQuizzes) { showQuizzes = it }
            PlannerToggleRow("Exams", showExams) { showExams = it }
            PlannerToggleRow("Announcements", showAnnouncements) { showAnnouncements = it }
            PlannerToggleRow("Materials", showMaterials) { showMaterials = it }
            PlannerToggleRow("Show completed", showCompleted) { showCompleted = it }
        }

        Text(
            text = "Course focus",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            LiquidGlassTextButton(text = "All courses", onClick = { selectedCourseId = null }, selected = selectedCourseId == null)
            availableCourseIds.forEach { (courseId, label) ->
                LiquidGlassTextButton(text = label, onClick = { selectedCourseId = courseId }, selected = selectedCourseId == courseId)
            }
        }
        LiquidGlassTextButton(
            text = "Apply filters",
            onClick = {
                onApply(
                    current.copy(
                        showTasks = showTasks,
                        showAssignments = showAssignments,
                        showQuizzes = showQuizzes,
                        showExams = showExams,
                        showAnnouncements = showAnnouncements,
                        showMaterials = showMaterials,
                        showCompleted = showCompleted,
                        courseId = selectedCourseId
                    )
                )
            }
        )
    }
}

@Composable
private fun PlannerToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
