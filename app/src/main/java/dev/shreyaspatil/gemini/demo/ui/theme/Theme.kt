package dev.shreyaspatil.gemini.demo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = primary,
    onPrimary =onPrimary,
    secondary = secondary,
    onSecondary = onSecondary,
    tertiary =tertiary,
    onTertiary = onTertiary,
    background = background,
    onBackground = onBackground,
)

@Composable
fun GeminiDemoTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}