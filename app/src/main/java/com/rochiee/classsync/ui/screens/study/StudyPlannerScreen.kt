package com.rochiee.classsync.ui.screens.study

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.study.StudyPlanEvent
import com.rochiee.classsync.bloc.study.StudyPlanState
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.StatusChip
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDate
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun

@Composable
fun StudyPlannerScreen(
    state: StudyPlanState,
    onEvent: (StudyPlanEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        ScreenSection(title = "Smart study planner", subtitle = "Rule-based study blocks generated from tasks, exams, and reading materials.") {
            LiquidGlassTextButton(
                text = if (state.isLoading) "Generating..." else "Generate plan",
                onClick = { onEvent(StudyPlanEvent.GeneratePlan) }
            )
        }
        val plan = state.plan
        if (plan == null || plan.items.isEmpty()) {
            EmptyState("No study plan yet", "Generate a plan to see suggested daily study blocks.")
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), modifier = Modifier.fillMaxWidth()) {
                ElevatedInfoCard(
                    title = "Blocks",
                    value = plan.items.size.toString(),
                    supportingText = "Study sessions currently scheduled",
                    modifier = Modifier.weight(1f),
                    accent = SkyBlue
                )
                ElevatedInfoCard(
                    title = "Done",
                    value = plan.items.count { it.isDone }.toString(),
                    supportingText = "Sessions you have already completed",
                    modifier = Modifier.weight(1f),
                    accent = MintGreen
                )
                ElevatedInfoCard(
                    title = "Upcoming",
                    value = plan.items.count { !it.isDone }.toString(),
                    supportingText = "Blocks still waiting for your focus",
                    modifier = Modifier.weight(1f),
                    accent = Sun
                )
            }
            TintedPanel {
                Text(
                    text = "Last generated ${plan.generatedAtMillis.formatDateTime()}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Your progress is now saved, so completed blocks remain checked after you reopen the app.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                items(plan.items) { item ->
                    TintedPanel {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                                    StatusChip(label = item.courseName, color = SkyBlue)
                                    StatusChip(label = item.sourceType, color = MintGreen)
                                    DeadlineChip(dueMillis = item.scheduledDateMillis, isCompleted = item.isDone)
                                }
                                Text(text = item.scheduledDateMillis.formatDate(), style = MaterialTheme.typography.bodyMedium)
                                Text(text = item.priorityExplanation, style = MaterialTheme.typography.bodyMedium)
                                Text(text = item.estimatedEffortLabel, style = MaterialTheme.typography.bodyMedium)
                            }
                            Checkbox(
                                checked = item.isDone,
                                onCheckedChange = { onEvent(StudyPlanEvent.ToggleBlockDone(item.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
