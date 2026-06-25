package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.planner.PlannerDay
import com.rochiee.classsync.planner.PlannerMonth
import com.rochiee.classsync.ui.components.DeadlineTone
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.deadlineToneFor
import com.rochiee.classsync.ui.components.formatDate
import com.rochiee.classsync.ui.theme.LocalSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val monthTitleFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
private val weekDayFormatter = SimpleDateFormat("EEE", Locale.getDefault())
private val dayNumberFormatter = SimpleDateFormat("d", Locale.getDefault())
private val monthCellWidth = 92.dp

@Composable
fun MonthPlannerView(
    month: PlannerMonth?,
    onSelectDay: (PlannerDay) -> Unit
) {
    val spacing = LocalSpacing.current
    if (month == null) {
        EmptyState("Month is empty", "Month view will appear once planner data is loaded.")
        return
    }

    val allDays = month.weeks.flatMap { it.days }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        TintedPanel {
            Text(
                text = monthTitleFormatter.format(Date(month.monthStartMillis)),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Monthly planning that adapts cleanly from compact phones to larger devices.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ResponsiveFlowRow(maxItemsInEachRow = 2) {
                MonthLegendChip(label = "Due today", color = DeadlineTone.TODAY.color)
                MonthLegendChip(label = "Due tomorrow", color = DeadlineTone.TOMORROW.color)
                MonthLegendChip(label = "Due soon", color = DeadlineTone.SOON.color)
                MonthLegendChip(label = "Upcoming", color = DeadlineTone.UPCOMING.color)
                MonthLegendChip(label = "On track", color = DeadlineTone.SAFE.color)
            }
        }

        BoxWithConstraints {
            if (maxWidth < 430.dp) {
                CompactMonthAgenda(days = allDays, onSelectDay = onSelectDay)
            } else {
                StandardMonthCalendar(month = month, onSelectDay = onSelectDay)
            }
        }
    }
}

@Composable
private fun StandardMonthCalendar(
    month: PlannerMonth,
    onSelectDay: (PlannerDay) -> Unit
) {
    val spacing = LocalSpacing.current
    val horizontalScroll = rememberScrollState()

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScroll),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            month.weeks.firstOrNull()?.days.orEmpty().forEach { day ->
                Box(
                    modifier = Modifier.width(monthCellWidth),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                week.days.forEach { day ->
                    MonthDayCell(
                        day = day,
                        modifier = Modifier.width(monthCellWidth),
                        compact = false,
                        onSelect = { onSelectDay(day) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactMonthAgenda(
    days: List<PlannerDay>,
    onSelectDay: (PlannerDay) -> Unit
) {
    val spacing = LocalSpacing.current

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        days.forEach { day ->
            CompactMonthDayRow(day = day, onSelect = { onSelectDay(day) })
        }
    }
}

@Composable
private fun CompactMonthDayRow(
    day: PlannerDay,
    onSelect: () -> Unit
) {
    val spacing = LocalSpacing.current
    val tone = day.primaryDeadlineTone()

    TintedPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = tone.color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayNumberFormatter.format(Date(day.dateStartMillis)),
                    style = MaterialTheme.typography.titleMedium,
                    color = tone.color,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Text(
                    text = day.dateStartMillis.formatDate(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${day.tasks.size} tasks • ${day.events.size} events • ${day.dueItems.size} due",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (day.dueItems.isNotEmpty()) {
                    Text(
                        text = day.dueItems.first().title,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = tone.color
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    day: PlannerDay,
    modifier: Modifier = Modifier,
    compact: Boolean,
    onSelect: () -> Unit
) {
    val tone = day.primaryDeadlineTone()
    val minHeight = if (compact) 112.dp else 138.dp

    TintedPanel(
        modifier = modifier
            .heightIn(min = minHeight)
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
            text = "${day.tasks.size} tasks",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${day.dueItems.size} due",
            style = MaterialTheme.typography.bodySmall,
            color = tone.color,
            maxLines = 1
        )
        if (day.highPriorityItems.isNotEmpty()) {
            Text(
                text = "${day.highPriorityItems.size} high priority",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (day.dueItems.isNotEmpty()) {
            Text(
                text = day.dueItems.first().title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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

private fun PlannerDay.primaryDeadlineTone(): DeadlineTone {
    return dueItems.minByOrNull { it.dueDateMillis ?: Long.MAX_VALUE }?.let {
        deadlineToneFor(it.dueDateMillis, it.isCompleted)
    } ?: DeadlineTone.NONE
}
