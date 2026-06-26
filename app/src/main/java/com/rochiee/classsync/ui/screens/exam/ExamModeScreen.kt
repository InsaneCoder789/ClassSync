package com.rochiee.classsync.ui.screens.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.exam.ExamModeScreenEvent
import com.rochiee.classsync.bloc.exam.ExamModeScreenState
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.StatusChip
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SkyBlue

@Composable
fun ExamModeScreen(
    state: ExamModeScreenState,
    onEvent: (ExamModeScreenEvent) -> Unit,
    onOpenStudyPlanner: () -> Unit
) {
    val spacing = LocalSpacing.current
    LaunchedEffect(Unit) {
        onEvent(ExamModeScreenEvent.LoadExamMode)
    }

    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        ScreenSection(title = "Exam mode", subtitle = "Focused preparation view for quizzes and exams.") {
            LiquidGlassTextButton(
                text = if (state.isLoading) "Refreshing..." else "Refresh exam mode",
                onClick = { onEvent(ExamModeScreenEvent.LoadExamMode) }
            )
        }

        val items = state.examMode?.upcomingExams.orEmpty()
        if (items.isEmpty()) {
            EmptyState("No upcoming exams", "Quizzes and exams will appear here once Classroom events are available.")
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), modifier = Modifier.fillMaxWidth()) {
                ElevatedInfoCard(
                    title = "Upcoming",
                    value = items.size.toString(),
                    supportingText = "Assessments currently in focus",
                    modifier = Modifier.weight(1f),
                    accent = SkyBlue
                )
                ElevatedInfoCard(
                    title = "Checklist done",
                    value = state.completedChecklist.values.sumOf { it.size }.toString(),
                    supportingText = "Prep actions already completed",
                    modifier = Modifier.weight(1f),
                    accent = MintGreen
                )
                ElevatedInfoCard(
                    title = "Immediate",
                    value = items.count { it.daysLeft <= 1 }.toString(),
                    supportingText = "Exams due today or tomorrow",
                    modifier = Modifier.weight(1f),
                    accent = Negative
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(items) { item ->
                    TintedPanel {
                        Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                            StatusChip(label = item.courseName, color = SkyBlue)
                            StatusChip(
                                label = item.countdownLabel,
                                color = if (item.daysLeft <= 1) Negative else SkyBlue
                            )
                        }
                        Text(text = "Pending tasks: ${item.pendingTaskTitles.joinToString().ifBlank { "None" }}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Related materials: ${item.relatedMaterialTitles.joinToString().ifBlank { "None" }}", style = MaterialTheme.typography.bodyMedium)
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                            item.checklist.forEach { checklistItem ->
                                androidx.compose.foundation.layout.Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = state.completedChecklist[item.eventId].orEmpty().contains(checklistItem),
                                        onCheckedChange = {
                                            onEvent(ExamModeScreenEvent.ToggleChecklistItem(item.eventId, checklistItem))
                                        }
                                    )
                                    Text(text = checklistItem, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        LiquidGlassTextButton(text = "Start study plan", onClick = onOpenStudyPlanner, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}