package dev.shreyaspatil.gemini.demo

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.gemini.demo.ui.screen.assistant.AssistantActivity
import dev.shreyaspatil.gemini.demo.ui.screen.imagecaption.ImageCaptionGenActivity
import dev.shreyaspatil.gemini.demo.ui.screen.simpleprompt.SimplePromptActivity
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme
import kotlin.reflect.KClass

class Menu<T : Activity>(val label: String, val activityClass: KClass<T>)

inline fun <reified T : Activity> menu(label: String) = Menu(label, T::class)

class MainActivity : ComposeActivity() {
    @Composable
    override fun RenderScreen() {
        MenuScreen(onNavigateTo = ::navigate)
    }

    override fun onResume() {
        super.onResume()
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 101)
    }

    private fun navigate(activityClass: KClass<out Activity>) {
        startActivity(Intent(this, activityClass.java))
    }
}

val menus = listOf(
    menu<SimplePromptActivity>("Simple Prompt"),
    menu<ImageCaptionGenActivity>("Image Caption Generate"),
    menu<AssistantActivity>("Assistant")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(onNavigateTo: (KClass<out Activity>) -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Gemini Demo")
            })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            menus.forEach { menu ->
                Menu(menu.label, onClick = { onNavigateTo(menu.activityClass) })
            }
        }
    }
}

@Composable
fun Menu(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GeminiDemoTheme {
        MenuScreen()
    }
}