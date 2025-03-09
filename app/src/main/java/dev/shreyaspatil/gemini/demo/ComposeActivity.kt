package dev.shreyaspatil.gemini.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme
import dev.shreyaspatil.gemini.demo.ui.theme.surface

abstract class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                surface.value.toInt(),
                surface.value.toInt(),
                detectDarkMode = { true }
            ),
            navigationBarStyle = SystemBarStyle.auto(
                surface.value.toInt(),
                surface.value.toInt(),
                detectDarkMode = { true }
            )
        )
        setContent {
            GeminiDemoTheme {
                RenderScreen()
            }
        }
    }

    @Composable
    abstract fun RenderScreen()
}