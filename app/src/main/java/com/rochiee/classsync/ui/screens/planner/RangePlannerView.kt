package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun RangePlannerView(
    days: List<PlannerDay>
) {
    val spacing = LocalSpacing.current
    if (days.isEmpty()) {
        EmptyState("No range selected", "Choose a date window to load a planner range.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            items(days) { day ->
                val tone = day.dueItems.minByOrNull { it.dueDateMillis ?: Long.MAX_VALUE }?.let {
                    deadlineToneFor(it.dueDateMillis, it.isCompleted)
                } ?: DeadlineTone.NONE
                TintedPanel {
                    Text(text = day.dateStartMillis.formatDate(), style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Text(
                            text = "${day.dueItems.size} due items",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tone.color
                        )
                        Text(
                            text = "${day.highPriorityItems.size} high-priority items",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
