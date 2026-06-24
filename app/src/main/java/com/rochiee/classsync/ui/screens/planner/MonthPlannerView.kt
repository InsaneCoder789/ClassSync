package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.planner.PlannerMonth
import com.rochiee.classsync.ui.components.DeadlineTone
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineToneFor
import com.rochiee.classsync.ui.theme.LocalSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val weekDayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
private val dayNumberFormatter = SimpleDateFormat("d", Locale.getDefault())

@Composable
fun MonthPlannerView(
    month: PlannerMonth?,
    onSelectDay: (PlannerDay) -> Unit
) {
    val spacing = LocalSpacing.current
    if (month == null) {
        EmptyState("Month is empty", "Month view will appear once planner data is loaded.")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            TintedPanel {
                Text(text = "Month overview", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "A richer calendar surface for deadlines, assessments, and hot academic days.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    MonthLegendChip(label = "Due today", color = DeadlineTone.TODAY.color, modifier = Modifier.weight(1f))
                    MonthLegendChip(label = "Due tomorrow", color = DeadlineTone.TOMORROW.color, modifier = Modifier.weight(1f))
                    MonthLegendChip(label = "Upcoming", color = DeadlineTone.NORMAL.color, modifier = Modifier.weight(1f))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                month.weeks.firstOrNull()?.days.orEmpty().forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = weekDayFormatter.format(Date(day.dateStartMillis)).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            month.weeks.forEach { week ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    week.days.forEach { day ->
                        MonthDayCell(
                            day = day,
                            modifier = Modifier.weight(1f),
                            onSelect = { onSelectDay(day) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    day: PlannerDay,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val tone = day.dueItems.minByOrNull { it.dueDateMillis ?: Long.MAX_VALUE }?.let {
        deadlineToneFor(it.dueDateMillis, it.isCompleted)
    } ?: DeadlineTone.NONE

    TintedPanel(
        modifier = modifier
            .heightIn(min = 124.dp)
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dayNumberFormatter.format(Date(day.dateStartMillis)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = if (tone == DeadlineTone.NONE) Color.Transparent else tone.color,
                        shape = RoundedCornerShape(99.dp)
                    )
            )
        }
        Text(
            text = "${day.tasks.size} tasks • ${day.events.size} events",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${day.dueItems.size} due",
            style = MaterialTheme.typography.bodyMedium,
            color = tone.color
        )
        Text(
            text = "${day.highPriorityItems.size} high priority",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (day.dueItems.isNotEmpty()) {
            Text(
                text = day.dueItems.first().title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MonthLegendChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(99.dp))
        )
        Text(
            text = "  $label",
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}
