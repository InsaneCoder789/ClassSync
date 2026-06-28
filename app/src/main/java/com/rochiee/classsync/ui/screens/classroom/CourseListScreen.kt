package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.dashboard.CourseDashboardSummary
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.ElevatedInfoCard
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ResponsiveFlowRow
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CourseListScreen(
    summaries: List<CourseDashboardSummary>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSelectCourse: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    var query by remember { mutableStateOf("") }
    val filteredSummaries = remember(summaries, query) {
        summaries.filter { summary ->
            val haystack = listOf(summary.courseName, summary.teacherName.orEmpty()).joinToString(" ").lowercase()
            query.isBlank() || haystack.contains(query.trim().lowercase())
        }
    }

    ScreenSection(title = "Semester 4 live classroom finder", subtitle = "Search your 4th semester courses, inspect live task pressure, and jump into course activity fast.") {
        ResponsiveFlowRow(maxItemsInEachRow = 1) {
            ElevatedInfoCard(
                title = "Semester",
                value = "4th",
                supportingText = "Live classroom finder view for your current term",
                modifier = Modifier.fillMaxWidth(),
                accent = SkyBlue
            )
            ElevatedInfoCard(
                title = "Courses",
                value = summaries.size.toString(),
                supportingText = "Synced classrooms currently available",
                modifier = Modifier.fillMaxWidth(),
                accent = MintGreen
            )
            ElevatedInfoCard(
                title = "Hot items",
                value = summaries.sumOf { it.overdueCount + it.pendingTaskCount }.toString(),
                supportingText = "Open work surfaced across the semester",
                modifier = Modifier.fillMaxWidth(),
                accent = Sun
            )
        }
        TintedPanel {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search classroom data") },
                placeholder = { Text("Find a course, teacher, or live activity") }
            )
            LiquidGlassTextButton(
                text = if (isRefreshing) "Refreshing..." else "Refresh Semester 4 Data",
                onClick = onRefresh,
                showArrow = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (filteredSummaries.isEmpty()) {
            EmptyState("No courses yet", "Run Classroom sync to populate your course list.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                filteredSummaries.forEach { summary ->
                    TintedPanel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCourse(summary.courseId) }
                    ) {
                        Text(
                            text = summary.courseName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        summary.teacherName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        ResponsiveFlowRow(maxItemsInEachRow = 3) {
                            MetricBadge(label = "Pending", value = summary.pendingTaskCount.toString(), color = SkyBlue)
                            MetricBadge(label = "Overdue", value = summary.overdueCount.toString(), color = Negative)
                            MetricBadge(label = "Assessments", value = summary.quizExamCount.toString(), color = Sun)
                            MetricBadge(label = "Updates", value = summary.announcementCount.toString(), color = MintGreen)
                        }
                        Text(
                            text = "Tap to inspect live tasks, materials, quizzes, and course signals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = spacing.xs)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBadge(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = Modifier.padding(end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}
