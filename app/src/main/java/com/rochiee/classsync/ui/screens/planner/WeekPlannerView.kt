package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.ui.components.DeadlineTone
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineToneFor
import com.rochiee.classsync.ui.components.formatDate
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun WeekPlannerView(
    days: List<PlannerDay>,
    onSelectDay: (PlannerDay) -> Unit
) {
    val spacing = LocalSpacing.current
    if (days.isEmpty()) {
        EmptyState("Week is empty", "Load your planner after syncing courses and tasks.")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            days.forEach { day ->
                val tone = day.dueItems.minByOrNull { it.dueDateMillis ?: Long.MAX_VALUE }?.let {
                    deadlineToneFor(it.dueDateMillis, it.isCompleted)
                } ?: DeadlineTone.NONE
                TintedPanel {
                    Text(text = day.dateStartMillis.formatDate(), style = MaterialTheme.typography.titleMedium)
                    Text(text = "${day.tasks.size} tasks • ${day.events.size} events", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Text(
                            text = "${day.dueItems.size} due",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tone.color
                        )
                        Text(
                            text = "${day.highPriorityItems.size} high priority",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Open day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = androidx.compose.ui.Modifier.clickable { onSelectDay(day) }
                    )
                }
            }
        }
    }
}
