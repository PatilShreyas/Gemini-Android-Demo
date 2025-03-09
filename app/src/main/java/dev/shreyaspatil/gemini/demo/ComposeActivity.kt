package dev.shreyaspatil.gemini.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme

abstract class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiDemoTheme {
                RenderScreen()
            }
        }
    }

    @Composable
    abstract fun RenderScreen()
}