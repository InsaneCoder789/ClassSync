package com.rochiee.classsync.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rochiee.classsync.ui.theme.SilverBorder
import com.rochiee.classsync.ui.theme.SilverBorderSoft
import kotlin.math.max

data class ProgressWidgetColors(
    val surfaceTop: Color,
    val surfaceBottom: Color,
    val border: Color,
    val title: Color,
    val body: Color,
    val muted: Color,
    val track: Color,
    val progressAccent: Color
)

@Composable
fun progressWidgetColors(
    darkMode: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
): ProgressWidgetColors {
    return if (darkMode) {
        ProgressWidgetColors(
            surfaceTop = Color(0xFF050505),
            surfaceBottom = Color(0xFF000000),
            border = SilverBorder.copy(alpha = 0.42f),
            title = Color(0xFFF7F8FC),
            body = Color(0xFFE7EBF7),
            muted = Color(0xFF99A2B5),
            track = Color(0xFF181818),
            progressAccent = MaterialTheme.colorScheme.primary
        )
    } else {
        ProgressWidgetColors(
            surfaceTop = Color(0xFFFFFFFF),
            surfaceBottom = Color(0xFFF7F7F3),
            border = SilverBorderSoft,
            title = Color(0xFF101828),
            body = Color(0xFF1F2937),
            muted = Color(0xFF667085),
            track = Color(0xFFE7E7E1),
            progressAccent = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ClassSyncProgressWidget(
    modifier: Modifier = Modifier,
    progressPercent: Int = 0,
    completedTasks: Int = 0,
    totalTasks: Int = 0,
    tasksLeft: Int = 0,
    progressCaption: String = "Live academic completion",
    darkMode: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
    val colors = progressWidgetColors(darkMode)
    val boundedPercent = progressPercent.coerceIn(0, 100)
    val stableTotal = max(totalTasks, completedTasks)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(colors.surfaceTop, colors.surfaceBottom)
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .border(
                width = 1.dp,
                color = colors.border,
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 22.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Progress pulse",
                color = colors.muted,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )

            HeroPieChart(
                percent = boundedPercent,
                colors = colors,
                modifier = Modifier.size(176.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PulseStatPill(
                    label = "Done",
                    value = completedTasks.toString(),
                    colors = colors
                )
                PulseStatPill(
                    label = "Active",
                    value = tasksLeft.toString(),
                    colors = colors
                )
                PulseStatPill(
                    label = "Total",
                    value = stableTotal.toString(),
                    colors = colors
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$completedTasks of $stableTotal finished",
                    color = colors.title,
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (tasksLeft == 0) {
                        "Everything is wrapped right now."
                    } else {
                        "$tasksLeft task${if (tasksLeft == 1) "" else "s"} still in motion"
                    },
                    color = colors.body,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = progressCaption,
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.82f)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completion rhythm",
                        color = colors.body,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${boundedPercent.coerceAtLeast(1)}% pace",
                        color = colors.muted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(
                            color = colors.track.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(1.dp, colors.border.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                )
                {
                    ProgressSparkline(
                        progressPercent = boundedPercent,
                        colors = colors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroPieChart(
    percent: Int,
    colors: ProgressWidgetColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 15.dp.toPx()
            val drawSize = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.progressAccent.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.minDimension * 0.54f
                ),
                radius = size.minDimension * 0.54f,
                center = center
            )

            drawCircle(
                color = colors.surfaceTop.copy(alpha = 0.95f),
                radius = size.minDimension * 0.30f,
                center = center
            )

            drawArc(
                color = colors.track,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(drawSize, drawSize),
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )

            drawArc(
                color = colors.progressAccent,
                startAngle = -90f,
                sweepAngle = if (percent >= 100) 360f else 360f * (percent / 100f),
                useCenter = false,
                topLeft = topLeft,
                size = Size(drawSize, drawSize),
                style = Stroke(
                    width = strokeWidth,
                    cap = if (percent >= 100) StrokeCap.Butt else StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "$percent%",
                color = colors.title,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "complete",
                color = colors.muted,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ProgressSparkline(
    progressPercent: Int,
    colors: ProgressWidgetColors,
    modifier: Modifier = Modifier
) {
    val bars = listOf(0.28f, 0.34f, 0.42f, 0.52f, 0.64f, 0.58f, 0.72f, 0.84f)
    val highlightIndex = ((bars.lastIndex) * (progressPercent / 100f)).toInt().coerceIn(0, bars.lastIndex)

    Canvas(modifier = modifier) {
        val gap = 8.dp.toPx()
        val barWidth = (size.width - gap * (bars.size - 1)) / bars.size

        bars.forEachIndexed { index, fraction ->
            val barHeight = size.height * fraction
            val x = index * (barWidth + gap)
            val y = size.height - barHeight
            val barColor = if (index <= highlightIndex) colors.progressAccent else colors.track

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

@Composable
private fun PulseStatPill(
    label: String,
    value: String,
    colors: ProgressWidgetColors
) {
    Column(
        modifier = Modifier
            .background(
                color = colors.track.copy(alpha = 0.6f),
                shape = RoundedCornerShape(18.dp)
            )
            .border(1.dp, colors.border.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            color = colors.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            color = colors.muted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
