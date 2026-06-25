package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rochiee.classsync.planner.PlannerItem
import com.rochiee.classsync.ui.components.DeadlineChip
import com.rochiee.classsync.ui.components.DeadlineText
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.PriorityChip
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun TodayPlannerView(
    items: List<PlannerItem>
) {
    val spacing = LocalSpacing.current
    if (items.isEmpty()) {
        EmptyState("No items", "No planner items for today.")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            items.forEach { item ->
                TintedPanel {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = item.courseName ?: "General", style = MaterialTheme.typography.bodyMedium)
                    ResponsiveFlowRow(maxItemsInEachRow = 2) {
                        DeadlineChip(dueMillis = item.dueDateMillis, isCompleted = item.isCompleted)
                        PriorityChip(priority = item.priority)
                    }
                    DeadlineText(
                        dueMillis = item.dueDateMillis,
                        isCompleted = item.isCompleted,
                        prefix = "Deadline "
                    )
                }
            }
        }
    }
}
