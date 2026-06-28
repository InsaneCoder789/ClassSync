package com.rochiee.classsync.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ProgressWidgetColors(
    val cardBackground: Color,
    val titleColor: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val ringTrack: Color,
    val ringProgress: Color,
    val barColor: Color,
    val iconColor: Color
)

@Composable
fun progressWidgetColors(
    darkMode: Boolean = isSystemInDarkTheme()
): ProgressWidgetColors {
    return if (darkMode) {
        ProgressWidgetColors(
            cardBackground = Color(0xFF151B2C),
            titleColor = Color(0xFFEAF0FF),
            primaryText = Color(0xFFF7F9FF),
            secondaryText = Color(0xFF9EA8C7),
            ringTrack = Color(0xFF26324A),
            ringProgress = Color(0xFF5EE0A2),
            barColor = Color(0xFF7EA7FF),
            iconColor = Color(0xFF6F7B99)
        )
    } else {
        ProgressWidgetColors(
            cardBackground = Color(0xFFF7F9FF),
            titleColor = Color(0xFF111B3D),
            primaryText = Color(0xFF101A3A),
            secondaryText = Color(0xFF7E86A1),
            ringTrack = Color(0xFFE5EAF4),
            ringProgress = Color(0xFF55C996),
            barColor = Color(0xFFBBD2FF),
            iconColor = Color(0xFFC6CEE0)
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
    darkMode: Boolean = isSystemInDarkTheme()
) {
    val colors = progressWidgetColors(darkMode)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .shadow(
                elevation = if (darkMode) 0.dp else 10.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = colors.cardBackground,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    color = colors.titleColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                TinyTrendIcon(
                    color = colors.iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressStat(
                    percent = progressPercent,
                    colors = colors,
                    modifier = Modifier.size(105.dp)
                )

                Spacer(modifier = Modifier.width(22.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$completedTasks / $totalTasks Tasks",
                        color = colors.primaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$tasksLeft Tasks left",
                        color = colors.primaryText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = progressCaption,
                        color = colors.secondaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    MiniBarChart(
                        color = colors.barColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularProgressStat(
    percent: Int,
    colors: ProgressWidgetColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 9.dp.toPx()
            val sizePx = size.minDimension
            val arcSize = Size(sizePx - strokeWidth, sizePx - strokeWidth)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            drawArc(
                color = colors.ringTrack,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = colors.ringProgress,
                startAngle = -210f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$percent%",
                color = colors.primaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Tasks done",
                color = colors.secondaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MiniBarChart(
    color: Color,
    modifier: Modifier = Modifier
) {
    val bars = listOf(
        0.18f, 0.28f, 0.52f, 0.34f, 0.72f,
        0.42f, 0.30f, 0.82f, 0.45f, 0.64f,
        0.36f, 0.78f
    )

    Canvas(modifier = modifier) {
        val barWidth = size.width / (bars.size * 2.4f)
        val gap = barWidth * 1.4f
        val maxHeight = size.height

        bars.forEachIndexed { index, value ->
            val left = index * (barWidth + gap)
            val barHeight = maxHeight * value
            val top = maxHeight - barHeight

            drawRoundRect(
                color = color.copy(alpha = 0.85f),
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    x = barWidth / 2,
                    y = barWidth / 2
                )
            )
        }
    }
}

@Composable
private fun TinyTrendIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(
            width = 2.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        val path = Path().apply {
            moveTo(size.width * 0.10f, size.height * 0.65f)
            lineTo(size.width * 0.30f, size.height * 0.45f)
            lineTo(size.width * 0.48f, size.height * 0.58f)
            lineTo(size.width * 0.68f, size.height * 0.32f)
            lineTo(size.width * 0.90f, size.height * 0.42f)
        }

        drawPath(
            path = path,
            color = color,
            style = stroke
        )
    }
}
