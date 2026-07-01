package com.rochiee.classsync.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

private val ColorWhite = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = ColorWhite,
    secondary = Sun,
    tertiary = Sun,
    background = ColorWhite,
    onBackground = Ink,
    surface = ColorWhite,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF5F5F2),
    onSurfaceVariant = Color(0xFF666055),
    error = Negative
)

private val DarkColorScheme = darkColorScheme(
    primary = MintGreen,
    onPrimary = Ink,
    secondary = NightAccent,
    tertiary = Sun,
    background = Color(0xFF000000),
    onBackground = Mist,
    surface = Color(0xFF000000),
    onSurface = Mist,
    surfaceVariant = Color(0xFF0D0D0D),
    onSurfaceVariant = Color(0xFFB4B8B0),
    error = Coral
)

private val ClassSyncShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(38.dp)
)

@Immutable
data class ClassSyncSpacing(
    val xs: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(4f),
    val sm: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(8f),
    val md: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(16f),
    val lg: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(24f),
    val xl: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(32f)
)

val LocalSpacing = androidx.compose.runtime.staticCompositionLocalOf { ClassSyncSpacing() }

@Composable
fun ClassSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    androidx.compose.runtime.CompositionLocalProvider(
        LocalSpacing provides ClassSyncSpacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = ClassSyncShapes,
            content = content
        )
    }
}
