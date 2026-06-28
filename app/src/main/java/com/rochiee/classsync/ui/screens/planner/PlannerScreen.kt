package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.classroom.ClassroomScreenState
import com.rochiee.classsync.bloc.planner.PlannerEvent
import com.rochiee.classsync.bloc.planner.PlannerState
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.ErrorState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.LoadingState
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDate
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SilverBorder
import com.rochiee.classsync.ui.theme.SkyBlue

private enum class PlannerMode {
    Today, Week, Month, Range
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlannerScreen(
    plannerState: PlannerState,
    classroomState: ClassroomScreenState,
    onPlannerEvent: (PlannerEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var mode by remember { mutableStateOf(PlannerMode.Today) }
    var showRangePicker by remember { mutableStateOf(false) }
    val selectedRangeLengthDays = remember(plannerState.selectedRangeStartMillis, plannerState.selectedRangeEndMillis) {
        val start = plannerState.selectedRangeStartMillis
        val end = plannerState.selectedRangeEndMillis
        if (start == null || end == null) {
            0
        } else {
            (((end - start) / (24L * 60L * 60L * 1000L)).toInt() + 1).coerceAtLeast(1)
        }
    }
    val snapshot = remember(mode, plannerState) {
        when (mode) {
            PlannerMode.Today -> {
                val day = plannerState.today
                PlannerSnapshot(
                    title = "Today focus",
                    total = day?.tasks?.size ?: 0,
                    due = day?.dueItems?.size ?: 0,
                    highPriority = day?.highPriorityItems?.size ?: 0,
                    supporting = "Use this lane to close work that is already live today."
                )
            }
            PlannerMode.Week -> {
                val week = plannerState.currentWeek
                PlannerSnapshot(
                    title = "Week balance",
                    total = week?.totalTaskCount ?: 0,
                    due = week?.overdueTaskCount ?: 0,
                    highPriority = week?.quizExamCount ?: 0,
                    supporting = "See how much work is stacking up across the next seven days."
                )
            }
            PlannerMode.Month -> {
                val month = plannerState.currentMonth
                PlannerSnapshot(
                    title = "Month coverage",
                    total = month?.totalTaskCount ?: 0,
                    due = month?.overdueTaskCount ?: 0,
                    highPriority = month?.quizExamCount ?: 0,
                    supporting = "Use the broader calendar to spot assessment pressure early."
                )
            }
            PlannerMode.Range -> {
                PlannerSnapshot(
                    title = "Range checkpoint",
                    total = plannerState.selectedRangeDays.sumOf { it.tasks.size },
                    due = plannerState.selectedRangeDays.sumOf { it.dueItems.size },
                    highPriority = plannerState.selectedRangeDays.sumOf { it.highPriorityItems.size },
                    supporting = "Custom windows help you isolate prep weeks, lab streaks, or exam runs."
                )
            }
        }
    }
    val rangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = plannerState.selectedRangeStartMillis ?: System.currentTimeMillis(),
        initialSelectedEndDateMillis = plannerState.selectedRangeEndMillis
            ?: (System.currentTimeMillis() + 2L * 24L * 60L * 60L * 1000L)
    )

    LaunchedEffect(mode) {
        when (mode) {
            PlannerMode.Today -> onPlannerEvent(PlannerEvent.LoadToday)
            PlannerMode.Week -> onPlannerEvent(PlannerEvent.LoadCurrentWeek)
            PlannerMode.Month -> onPlannerEvent(PlannerEvent.LoadCurrentMonth)
            PlannerMode.Range -> {
                val now = System.currentTimeMillis()
                onPlannerEvent(PlannerEvent.LoadRange(now, now + 2L * 24L * 60L * 60L * 1000L))
            }
        }
    }

    if (showRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = rangePickerState.selectedStartDateMillis
                        val end = rangePickerState.selectedEndDateMillis
                        if (start != null && end != null) {
                            onPlannerEvent(PlannerEvent.LoadRange(start, end))
                        }
                        showRangePicker = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = rangePickerState,
                title = { Text("Pick a custom range") },
                showModeToggle = false
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.md)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        item {
            TintedPanel {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    SilverBorder.copy(alpha = 0.18f)
                                )
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "PLANNER",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Plan by today, week, month, or a custom range.",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "This view keeps short-term deadlines, assessments, and schedule pressure in one place.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    LiquidGlassTextButton(text = "Today", onClick = { mode = PlannerMode.Today }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Today)
                    LiquidGlassTextButton(text = "This week", onClick = { mode = PlannerMode.Week }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Week)
                    LiquidGlassTextButton(text = "This month", onClick = { mode = PlannerMode.Month }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Month)
                    LiquidGlassTextButton(text = "Custom range", onClick = { mode = PlannerMode.Range }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Range)
                }
            }
        }

        item {
            BoxWithConstraints {
                if (maxWidth < 420.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                            ElevatedInfoCard(
                                title = snapshot.title,
                                value = snapshot.total.toString(),
                                supportingText = snapshot.supporting,
                                modifier = Modifier.weight(1f),
                                accent = SkyBlue
                            )
                            ElevatedInfoCard(
                                title = "Needs attention",
                                value = snapshot.due.toString(),
                                supportingText = "Due now, overdue, or inside the active planning window",
                                modifier = Modifier.weight(1f),
                                accent = Negative
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), modifier = Modifier.fillMaxWidth()) {
                            ElevatedInfoCard(
                                title = "High pressure",
                                value = snapshot.highPriority.toString(),
                                supportingText = "High-priority tasks, quizzes, or exams in the current view",
                                modifier = Modifier.weight(1f),
                                accent = MintGreen
                            )
                            ElevatedInfoCard(
                                title = "Range ready",
                                value = selectedRangeLengthDays.coerceAtLeast(1).toString(),
                                supportingText = if (mode == PlannerMode.Range) {
                                    "Days currently loaded in your custom planning window"
                                } else {
                                    "Switch to range mode when you want a tighter prep window"
                                },
                                modifier = Modifier.weight(1f),
                                accent = SilverBorder
                            )
                        }
                    }
                } else {
                    ResponsiveFlowRow(maxItemsInEachRow = 2) {
                        ElevatedInfoCard(
                            title = snapshot.title,
                            value = snapshot.total.toString(),
                            supportingText = snapshot.supporting,
                            modifier = Modifier.fillMaxWidth(),
                            accent = SkyBlue
                        )
                        ElevatedInfoCard(
                            title = "Needs attention",
                            value = snapshot.due.toString(),
                            supportingText = "Due now, overdue, or inside the active planning window",
                            modifier = Modifier.fillMaxWidth(),
                            accent = Negative
                        )
                        ElevatedInfoCard(
                            title = "High pressure",
                            value = snapshot.highPriority.toString(),
                            supportingText = "High-priority tasks, quizzes, or exams in the current view",
                            modifier = Modifier.fillMaxWidth(),
                            accent = MintGreen
                        )
                        ElevatedInfoCard(
                            title = "Range ready",
                            value = selectedRangeLengthDays.coerceAtLeast(1).toString(),
                            supportingText = if (mode == PlannerMode.Range) {
                                "Days currently loaded in your custom planning window"
                            } else {
                                "Switch to range mode when you want a tighter prep window"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            accent = SilverBorder
                        )
                    }
                }
            }
        }

        when (mode) {
            PlannerMode.Today -> {
                val day = plannerState.today
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        ElevatedInfoCard(
                            title = "Due today",
                            value = (day?.dueItems?.size ?: 0).toString(),
                            supportingText = "Items that need attention before tonight",
                            modifier = Modifier.weight(1f),
                            accent = Negative
                        )
                        ElevatedInfoCard(
                            title = "Priority lane",
                            value = (day?.highPriorityItems?.size ?: 0).toString(),
                            supportingText = "Important tasks identified for today",
                            modifier = Modifier.weight(1f),
                            accent = SkyBlue
                        )
                    }
                }
            }

            PlannerMode.Week -> {
                val week = plannerState.currentWeek
                item {
                    ResponsiveFlowRow(maxItemsInEachRow = 2) {
                        ElevatedInfoCard(
                            title = "Week load",
                            value = (week?.totalTaskCount ?: 0).toString(),
                            supportingText = "Scheduled work across this week",
                            modifier = Modifier.fillMaxWidth(),
                            accent = SkyBlue
                        )
                        ElevatedInfoCard(
                            title = "Overdue",
                            value = (week?.overdueTaskCount ?: 0).toString(),
                            supportingText = "Items already running behind",
                            modifier = Modifier.fillMaxWidth(),
                            accent = Negative
                        )
                    }
                }
            }

            PlannerMode.Month -> {
                val month = plannerState.currentMonth
                item { ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    ElevatedInfoCard(
                        title = "Month load",
                        value = (month?.totalTaskCount ?: 0).toString(),
                        supportingText = "Tasks visible in the current month",
                        modifier = Modifier.fillMaxWidth(),
                        accent = SkyBlue
                    )
                    ElevatedInfoCard(
                        title = "Assessments",
                        value = (month?.quizExamCount ?: 0).toString(),
                        supportingText = "Quizzes and exams currently in view",
                        modifier = Modifier.fillMaxWidth(),
                        accent = MintGreen
                    )
                } }
            }

            PlannerMode.Range -> {
                item { ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    ElevatedInfoCard(
                        title = "Range span",
                        value = selectedRangeLengthDays.toString(),
                        supportingText = "Custom planning window currently active",
                        modifier = Modifier.fillMaxWidth(),
                        accent = SkyBlue
                    )
                    ElevatedInfoCard(
                        title = "Due inside range",
                        value = plannerState.selectedRangeDays.sumOf { it.dueItems.size }.toString(),
                        supportingText = "Deadlines currently inside the range",
                        modifier = Modifier.fillMaxWidth(),
                        accent = Negative
                    )
                } }
            }
        }

        item {
            PlannerFilterSheet(
                current = plannerState.activeFilter,
                onApply = { filter ->
                    onPlannerEvent(PlannerEvent.SetFilter(filter))
                    when (mode) {
                        PlannerMode.Today -> onPlannerEvent(PlannerEvent.LoadToday)
                        PlannerMode.Week -> onPlannerEvent(PlannerEvent.LoadCurrentWeek)
                        PlannerMode.Month -> onPlannerEvent(PlannerEvent.LoadCurrentMonth)
                        PlannerMode.Range -> {
                            val start = plannerState.selectedRangeStartMillis ?: System.currentTimeMillis()
                            val end = plannerState.selectedRangeEndMillis ?: (start + 2L * 24L * 60L * 60L * 1000L)
                            onPlannerEvent(PlannerEvent.LoadRange(start, end))
                        }
                    }
                }
            )
        }

        if (mode == PlannerMode.Range) {
            item {
                TintedPanel {
                    Text(
                        text = "Selected range",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${plannerState.selectedRangeStartMillis.formatDate()} to ${plannerState.selectedRangeEndMillis.formatDate()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LiquidGlassTextButton(
                        text = "Pick custom dates",
                        onClick = { showRangePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        selected = true
                    )
                }
            }
            item {
                ResponsiveFlowRow(maxItemsInEachRow = 2) {
                    LiquidGlassTextButton(
                        text = "Next 3 days",
                        onClick = {
                            val now = System.currentTimeMillis()
                            onPlannerEvent(PlannerEvent.LoadRange(now, now + 2L * 24L * 60L * 60L * 1000L))
                        },
                        modifier = Modifier.widthIn(min = 132.dp)
                    )
                    LiquidGlassTextButton(
                        text = "Next 7 days",
                        onClick = {
                            val now = System.currentTimeMillis()
                            onPlannerEvent(PlannerEvent.LoadRange(now, now + 6L * 24L * 60L * 60L * 1000L))
                        },
                        modifier = Modifier.widthIn(min = 132.dp)
                    )
                    LiquidGlassTextButton(
                        text = "Next 14 days",
                        onClick = {
                            val now = System.currentTimeMillis()
                            onPlannerEvent(PlannerEvent.LoadRange(now, now + 13L * 24L * 60L * 60L * 1000L))
                        },
                        modifier = Modifier.widthIn(min = 132.dp)
                    )
                }
            }
        }

        when (mode) {
            PlannerMode.Today -> {
                val items = (plannerState.today?.tasks ?: emptyList()) + (plannerState.today?.events ?: emptyList())
                item {
                    when {
                        plannerState.isLoading && items.isEmpty() -> LoadingState("Loading today's planner")
                        plannerState.errorMessage != null && items.isEmpty() -> ErrorState(plannerState.errorMessage)
                        else -> TodayPlannerView(items = items)
                    }
                }
            }
            PlannerMode.Week -> {
                item {
                    val days = plannerState.currentWeek?.days ?: emptyList()
                    when {
                        plannerState.isLoading && days.isEmpty() -> LoadingState("Loading this week's planner")
                        plannerState.errorMessage != null && days.isEmpty() -> ErrorState(plannerState.errorMessage)
                        else -> WeekPlannerView(
                            days = days,
                            onSelectDay = { day ->
                                mode = PlannerMode.Range
                                onPlannerEvent(PlannerEvent.LoadRange(day.dateStartMillis, day.dateEndMillis))
                            }
                        )
                    }
                }
            }
            PlannerMode.Month -> {
                item {
                    when {
                        plannerState.isLoading && plannerState.currentMonth == null -> LoadingState("Loading this month")
                        plannerState.errorMessage != null && plannerState.currentMonth == null -> ErrorState(plannerState.errorMessage)
                        else -> MonthPlannerView(
                            month = plannerState.currentMonth,
                            onSelectDay = { day ->
                                mode = PlannerMode.Range
                                onPlannerEvent(PlannerEvent.LoadRange(day.dateStartMillis, day.dateEndMillis))
                            }
                        )
                    }
                }
            }
            PlannerMode.Range -> {
                item {
                    when {
                        plannerState.isLoading && plannerState.selectedRangeDays.isEmpty() -> LoadingState("Loading selected range")
                        plannerState.errorMessage != null && plannerState.selectedRangeDays.isEmpty() -> ErrorState(plannerState.errorMessage)
                        else -> RangePlannerView(days = plannerState.selectedRangeDays)
                    }
                }
            }
        }
    }
}

private data class PlannerSnapshot(
    val title: String,
    val total: Int,
    val due: Int,
    val highPriority: Int,
    val supporting: String
)
