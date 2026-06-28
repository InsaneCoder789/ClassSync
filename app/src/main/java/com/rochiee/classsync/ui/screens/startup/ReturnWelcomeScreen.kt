package com.rochiee.classsync.ui.screens.startup

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.blur
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rochiee.classsync.R
import com.rochiee.classsync.ui.theme.Ink
import com.rochiee.classsync.ui.theme.Mist
import com.rochiee.classsync.ui.theme.SilverBorder
import com.rochiee.classsync.ui.theme.SkyBlue

@Composable
fun ReturnWelcomeScreen(
    darkTheme: Boolean,
    durationMillis: Int,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val shimmer = rememberInfiniteTransition(label = "return-welcome")
    val orbScale = shimmer.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb-scale"
    )
    val orbAlpha = shimmer.animateFloat(
        initialValue = if (darkTheme) 0.34f else 0.2f,
        targetValue = if (darkTheme) 0.52f else 0.32f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb-alpha"
    )
    val highlightShift = shimmer.animateFloat(
        initialValue = -0.25f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "highlight-shift"
    )

    LaunchedEffect(durationMillis) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)
        )
    }

    val backgroundBrush = if (darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF040507),
                Ink,
                Color(0xFF0D1015)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8FAFC),
                Mist,
                Color(0xFFE7EBF1)
            )
        )
    }
    val titleColor = if (darkTheme) Mist else Ink
    val subtitleColor = if (darkTheme) Mist.copy(alpha = 0.72f) else Ink.copy(alpha = 0.62f)
    val progressTrack = if (darkTheme) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f)
    val progressBar = if (darkTheme) SilverBorder else SkyBlue
    val progressGlow = if (darkTheme) SilverBorder.copy(alpha = 0.5f) else Color(0xFF96A7C1).copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(260.dp)
                .scale(orbScale.value)
                .alpha(orbAlpha.value)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (darkTheme) {
                            listOf(
                                SilverBorder.copy(alpha = 0.42f),
                                Color.Transparent
                            )
                        } else {
                            listOf(
                                Color(0xFFB7C4D7).copy(alpha = 0.42f),
                                Color.Transparent
                            )
                        }
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 56.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "ClassSync logo",
                    modifier = Modifier
                        .size(156.dp)
                        .graphicsLayer {
                            scaleX = 0.985f + (progress.value * 0.015f)
                            scaleY = 0.985f + (progress.value * 0.015f)
                            alpha = 0.92f + (progress.value * 0.08f)
                        }
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ClassSync",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.6).sp
                        ),
                        color = titleColor
                    )
                    Text(
                        text = "Everything in sync.\nEvery goal in reach.",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 32.sp
                        ),
                        color = titleColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Preparing your academic workspace.",
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                        color = subtitleColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(progress.value * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = subtitleColor,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(progressTrack)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value.coerceIn(0f, 1f))
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        progressBar.copy(alpha = 0.92f),
                                        progressBar,
                                        progressBar.copy(alpha = 0.84f)
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = ((highlightShift.value - 0.2f) * 260).dp)
                            .fillMaxWidth(0.18f)
                            .height(10.dp)
                            .blur(10.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        progressGlow,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
                Text(
                    text = "Loading your workspace",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
