package com.rochiee.classsync.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.rochiee.classsync.bloc.eventdetail.EventDetailEvent
import com.rochiee.classsync.bloc.eventdetail.EventDetailState
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.LiquidGlassTextButton
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun EventDetailScreen(
    eventId: String,
    state: EventDetailState,
    onEvent: (EventDetailEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    LaunchedEffect(eventId) {
        onEvent(EventDetailEvent.LoadEvent(eventId))
    }

    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        val event = state.event
        if (event == null) {
            EmptyState("Event unavailable", state.errorMessage ?: "The event details could not be loaded.")
        } else {
            ScreenSection(title = event.title, subtitle = event.courseName ?: "General") {
                TintedPanel {
                    Text(text = event.description ?: event.originalText.orEmpty(), style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Received ${event.eventTimeMillis.formatDateTime()}", style = MaterialTheme.typography.bodyMedium)
                    event.dueDateMillis?.let {
                        Text(text = "Due ${it.formatDateTime()}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            ScreenSection(title = "Summary", subtitle = "Optional AI foundation for long announcements and materials.") {
                TintedPanel {
                    LiquidGlassTextButton(
                        text = if (state.isSummarizing) "Summarizing..." else "Summarize",
                        onClick = { onEvent(EventDetailEvent.SummarizeEvent(eventId)) }
                    )
                    state.summaryResult?.let { summary ->
                        Text(text = summary.shortSummary, style = MaterialTheme.typography.bodyLarge)
                        if (summary.actionItems.isNotEmpty()) {
                            Text(text = "Action items: ${summary.actionItems.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (summary.deadlineHints.isNotEmpty()) {
                            Text(text = "Deadline hints: ${summary.deadlineHints.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(text = "Importance: ${summary.importanceLevel}", style = MaterialTheme.typography.bodyMedium)
                    }
                    state.errorMessage?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
