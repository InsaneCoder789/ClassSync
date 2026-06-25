package com.rochiee.classsync.ui.screens.planner

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.bloc.classroom.ClassroomScreenState
import com.rochiee.classsync.bloc.planner.PlannerEvent
import com.rochiee.classsync.bloc.planner.PlannerState
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDate
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun

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

    Column(
        modifier = Modifier
            .padding(spacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        ScreenSection(title = "Planner", subtitle = "Today, weekly, and monthly views on top of the existing planner backend.") {
            ResponsiveFlowRow(maxItemsInEachRow = 2) {
                LiquidGlassTextButton(text = "Today", onClick = { mode = PlannerMode.Today }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Today)
                LiquidGlassTextButton(text = "Week", onClick = { mode = PlannerMode.Week }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Week)
                LiquidGlassTextButton(text = "Month", onClick = { mode = PlannerMode.Month }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Month)
                LiquidGlassTextButton(text = "Range", onClick = { mode = PlannerMode.Range }, modifier = Modifier.widthIn(min = 136.dp), selected = mode == PlannerMode.Range)
            }
        }

        when (mode) {
            PlannerMode.Today -> {
                val day = plannerState.today
                ResponsiveFlowRow(maxItemsInEachRow = 1) {
                    ElevatedInfoCard(
                        title = "Due today",
                        value = (day?.dueItems?.size ?: 0).toString(),
                        supportingText = "Items that need attention before tonight",
                        modifier = Modifier.fillMaxWidth(),
                        accent = Negative
                    )
                    ElevatedInfoCard(
                        title = "High priority",
                        value = (day?.highPriorityItems?.size ?: 0).toString(),
                        supportingText = "Hot items surfaced by the planner",
                        modifier = Modifier.fillMaxWidth(),
                        accent = Sun
                    )
                }
            }
            PlannerMode.Week -> {
                val week = plannerState.currentWeek
                ResponsiveFlowRow(maxItemsInEachRow = 1) {
                    ElevatedInfoCard(
                        title = "Week tasks",
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
            PlannerMode.Month -> {
                val month = plannerState.currentMonth
                ResponsiveFlowRow(maxItemsInEachRow = 1) {
                    ElevatedInfoCard(
                        title = "Month tasks",
                        value = (month?.totalTaskCount ?: 0).toString(),
                        supportingText = "Tasks visible in the current month",
                        modifier = Modifier.fillMaxWidth(),
                        accent = SkyBlue
                    )
                    ElevatedInfoCard(
                        title = "Assessments",
                        value = (month?.quizExamCount ?: 0).toString(),
                        supportingText = "Quizzes and exams in view",
                        modifier = Modifier.fillMaxWidth(),
                        accent = Sun
                    )
                }
            }
            PlannerMode.Range -> {
                ResponsiveFlowRow(maxItemsInEachRow = 1) {
                    ElevatedInfoCard(
                        title = "Range days",
                        value = selectedRangeLengthDays.toString(),
                        supportingText = "Custom planning window currently active",
                        modifier = Modifier.fillMaxWidth(),
                        accent = SkyBlue
                    )
                    ElevatedInfoCard(
                        title = "Due items",
                        value = plannerState.selectedRangeDays.sumOf { it.dueItems.size }.toString(),
                        supportingText = "Deadlines currently inside the range",
                        modifier = Modifier.fillMaxWidth(),
                        accent = Negative
                    )
                }
            }
        }

        PlannerFilterSheet(
            current = plannerState.activeFilter,
            availableCourseIds = classroomState.catalog.semesters
                .flatMap { semester -> semester.sections.map { section -> section.sectionId to section.sectionId } },
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

        if (mode == PlannerMode.Range) {
            TintedPanel {
                Text(
                    text = "Selected range: ${plannerState.selectedRangeStartMillis.formatDate()} to ${plannerState.selectedRangeEndMillis.formatDate()}",
                )
                LiquidGlassTextButton(text = "Pick custom dates", onClick = { showRangePicker = true }, modifier = Modifier.fillMaxWidth())
            }
            ResponsiveFlowRow(maxItemsInEachRow = 2) {
                LiquidGlassTextButton(
                    text = "Next 3 Days",
                    onClick = {
                        val now = System.currentTimeMillis()
                        onPlannerEvent(PlannerEvent.LoadRange(now, now + 2L * 24L * 60L * 60L * 1000L))
                    },
                    modifier = Modifier.widthIn(min = 132.dp)
                )
                LiquidGlassTextButton(
                    text = "Next 7 Days",
                    onClick = {
                        val now = System.currentTimeMillis()
                        onPlannerEvent(PlannerEvent.LoadRange(now, now + 6L * 24L * 60L * 60L * 1000L))
                    },
                    modifier = Modifier.widthIn(min = 132.dp)
                )
                LiquidGlassTextButton(
                    text = "Next 14 Days",
                    onClick = {
                        val now = System.currentTimeMillis()
                        onPlannerEvent(PlannerEvent.LoadRange(now, now + 13L * 24L * 60L * 60L * 1000L))
                    },
                    modifier = Modifier.widthIn(min = 132.dp)
                )
            }
        }

        when (mode) {
            PlannerMode.Today -> {
                val items = (plannerState.today?.tasks ?: emptyList()) + (plannerState.today?.events ?: emptyList())
                TodayPlannerView(items = items)
            }
            PlannerMode.Week -> {
                WeekPlannerView(
                    days = plannerState.currentWeek?.days ?: emptyList(),
                    onSelectDay = { day ->
                        mode = PlannerMode.Range
                        onPlannerEvent(PlannerEvent.LoadRange(day.dateStartMillis, day.dateEndMillis))
                    }
                )
            }
            PlannerMode.Month -> {
                MonthPlannerView(
                    month = plannerState.currentMonth,
                    onSelectDay = { day ->
                        mode = PlannerMode.Range
                        onPlannerEvent(PlannerEvent.LoadRange(day.dateStartMillis, day.dateEndMillis))
                    }
                )
            }
            PlannerMode.Range -> {
                RangePlannerView(days = plannerState.selectedRangeDays)
            }
        }
    }
}
