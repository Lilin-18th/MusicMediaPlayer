package com.lilin.musicmediaplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colorScheme = darkColorScheme(
    primary = PlayerAccent,
    onPrimary = Color.White,
    secondary = PlayerGlowPurple,
    onSecondary = Color.White,
    tertiary = PlayerGlowBlue,
    onTertiary = Color.White,
    background = PlayerBackgroundBottom,
    onBackground = Color.White,
    surface = PlayerSurfaceVariant,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF9E9E9E),
    error = Color(0xFFCF6679),
    onError = Color.Black,
)

@Composable
fun MusicMediaPlayerTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
