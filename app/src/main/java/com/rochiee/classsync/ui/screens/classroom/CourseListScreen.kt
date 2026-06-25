package com.rochiee.classsync.ui.screens.classroom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.dashboard.CourseDashboardSummary
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.SkyBlue

@Composable
fun CourseListScreen(
    summaries: List<CourseDashboardSummary>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSelectCourse: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    ScreenSection(title = "Classroom", subtitle = "Course-wise organization powered by local course, task, and event data.") {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            LiquidGlassTextButton(text = if (isRefreshing) "Refreshing..." else "Refresh Courses", onClick = onRefresh)
        }
        if (summaries.isEmpty()) {
            EmptyState("No courses yet", "Run Classroom sync to populate your course list.")
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                summaries.forEach { summary ->
                    TintedPanel(
                        modifier = Modifier
                            .width(220.dp)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            Column {
                                Text("Pending", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(summary.pendingTaskCount.toString(), style = MaterialTheme.typography.titleMedium, color = SkyBlue)
                            }
                            Column {
                                Text("Overdue", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(summary.overdueCount.toString(), style = MaterialTheme.typography.titleMedium, color = Negative)
                            }
                        }
                    }
                }
            }
        }
    }
}
