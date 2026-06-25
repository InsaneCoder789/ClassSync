package com.rochiee.classsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rochiee.classsync.domain.model.AcademicTask
import com.rochiee.classsync.domain.model.TaskPriority
import com.rochiee.classsync.ui.theme.AlertOrange
import com.rochiee.classsync.ui.theme.CautionYellow
import com.rochiee.classsync.ui.theme.DeepNegative
import com.rochiee.classsync.ui.theme.LocalSpacing
import com.rochiee.classsync.ui.theme.MintGreen
import com.rochiee.classsync.ui.theme.Negative
import com.rochiee.classsync.ui.theme.Positive
import com.rochiee.classsync.ui.theme.SafeGreen
import com.rochiee.classsync.ui.theme.SkyBlue
import com.rochiee.classsync.ui.theme.Sun
import com.rochiee.classsync.ui.theme.Warning
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ScreenSection(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            subtitle?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        content()
    }
}

@Composable
fun ElevatedInfoCard(
    title: String,
    value: String,
    supportingText: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.68f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.32f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = spacing.md, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .background(accent.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusChip(label: String, color: Color) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = color.copy(alpha = 0.16f),
            disabledLabelColor = color
        )
    )
}

enum class DeadlineTone(
    val label: String,
    val color: Color
) {
    OVERDUE("Overdue", DeepNegative),
    TODAY("Due today", DeepNegative),
    TOMORROW("Due tomorrow", Negative),
    SOON("Due soon", AlertOrange),
    UPCOMING("Upcoming", CautionYellow),
    SAFE("On track", SafeGreen),
    COMPLETE("Done", Positive),
    NONE("No date", Color(0xFF7B8794))
}

fun deadlineToneFor(
    dueMillis: Long?,
    isCompleted: Boolean,
    nowMillis: Long = System.currentTimeMillis()
): DeadlineTone {
    if (isCompleted) return DeadlineTone.COMPLETE
    if (dueMillis == null) return DeadlineTone.NONE

    val startOfToday = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val startOfTomorrow = startOfToday + 24L * 60L * 60L * 1000L
    val startOfDayAfterTomorrow = startOfTomorrow + 24L * 60L * 60L * 1000L

    return when {
        dueMillis < nowMillis -> DeadlineTone.OVERDUE
        dueMillis < startOfTomorrow -> DeadlineTone.TODAY
        dueMillis < startOfDayAfterTomorrow -> DeadlineTone.TOMORROW
        dueMillis < nowMillis + 3L * 24L * 60L * 60L * 1000L -> DeadlineTone.SOON
        dueMillis < nowMillis + 7L * 24L * 60L * 60L * 1000L -> DeadlineTone.UPCOMING
        else -> DeadlineTone.SAFE
    }
}

@Composable
fun DeadlineChip(
    dueMillis: Long?,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val tone = remember(dueMillis, isCompleted) {
        deadlineToneFor(dueMillis = dueMillis, isCompleted = isCompleted)
    }
    StatusChip(label = tone.label, color = tone.color)
}

@Composable
fun DeadlineText(
    dueMillis: Long?,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    prefix: String = "Due "
) {
    val tone = remember(dueMillis, isCompleted) {
        deadlineToneFor(dueMillis = dueMillis, isCompleted = isCompleted)
    }
    Text(
        text = "$prefix${dueMillis.formatDate()}",
        style = MaterialTheme.typography.bodyMedium,
        color = tone.color,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PriorityChip(priority: TaskPriority) {
    val (label, color) = when (priority) {
        TaskPriority.HIGH -> "High" to Negative
        TaskPriority.MEDIUM -> "Medium" to Sun
        TaskPriority.LOW -> "Low" to MintGreen
        TaskPriority.URGENT -> "Urgent" to Negative
    }
    StatusChip(label = label, color = color)
}

@Composable
fun CourseChip(courseName: String) {
    StatusChip(label = courseName, color = SkyBlue)
}

@Composable
fun EmptyState(title: String, description: String) {
    val spacing = LocalSpacing.current
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.62f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun LoadingState(label: String) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.width(spacing.sm))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ErrorState(message: String) {
    EmptyState(title = "Something needs attention", description = message)
}

@Composable
fun StatRow(label: String, value: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val contentModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }
    Row(
        modifier = contentModifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun TintedPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.68f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.28f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = spacing.md, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            content()
        }
    }
}

@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val baseColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.24f)
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.34f)
    } else {
        Color.White.copy(alpha = if (enabled) 0.42f else 0.24f)
    }
    val contentColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .clickable(enabled = enabled, onClick = onClick),
        color = Color.Transparent,
        contentColor = contentColor,
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            baseColor.copy(alpha = 0.92f),
                            baseColor.copy(alpha = 0.72f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(26.dp))
                .padding(horizontal = 16.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun LiquidGlassTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    showArrow: Boolean = text.length > 4
) {
    LiquidGlassButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        selected = selected
    ) {
        if (showArrow) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = LocalContentColor.current,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            ButtonArrowBadge()
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = LocalContentColor.current,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResponsiveFlowRow(
    modifier: Modifier = Modifier,
    maxItemsInEachRow: Int,
    content: @Composable FlowRowScope.() -> Unit
) {
    val spacing = LocalSpacing.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = maxItemsInEachRow,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        content()
    }
}

@Composable
fun ButtonArrowBadge(
    modifier: Modifier = Modifier,
    dark: Boolean = true
) {
    val background = if (dark) Color(0xFF122B52) else Color.White.copy(alpha = 0.22f)
    val foreground = if (dark) Color.White else Color(0xFF122B52)
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(18.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = foreground
        )
    }
}

@Composable
fun AppLogoLockup(
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitle: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.rochiee.classsync.R.mipmap.ic_launcher),
                contentDescription = "ClassSync logo",
                modifier = Modifier.width(34.dp).height(34.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "ClassSync",
                style = MaterialTheme.typography.titleLarge,
                color = titleColor,
                fontWeight = FontWeight.SemiBold
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun AcademicTask.deadlineTone(nowMillis: Long = System.currentTimeMillis()): DeadlineTone {
    return deadlineToneFor(dueMillis = dueDate, isCompleted = isCompleted, nowMillis = nowMillis)
}

fun Long?.formatDateTime(): String {
    if (this == null) return "Not set"
    return SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(this))
}

fun Long?.formatDate(): String {
    if (this == null) return "No date"
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))
}
