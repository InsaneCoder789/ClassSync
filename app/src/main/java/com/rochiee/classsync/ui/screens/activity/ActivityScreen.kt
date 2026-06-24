package com.rochiee.classsync.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import com.rochiee.classsync.bloc.event.EventState
import com.rochiee.classsync.ui.components.EmptyState
import com.rochiee.classsync.ui.components.ScreenSection
import com.rochiee.classsync.ui.components.TintedPanel
import com.rochiee.classsync.ui.components.formatDateTime
import com.rochiee.classsync.ui.theme.LocalSpacing

@Composable
fun ActivityScreen(
    eventState: EventState,
    onOpenEvent: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        ScreenSection(title = "Recent activity", subtitle = "Latest classroom events stored locally.") {
            if (eventState.recentEvents.isEmpty()) {
                EmptyState("No activity yet", "Run a sync or add sample events from Debug to populate this timeline.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    items(eventState.recentEvents) { event ->
                        TintedPanel {
                            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = event.courseName ?: "General", style = MaterialTheme.typography.bodyMedium)
                            Text(text = event.eventTimeMillis.formatDateTime(), style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "Open details",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .padding(top = spacing.xs)
                                    .clickable { onOpenEvent(event.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
