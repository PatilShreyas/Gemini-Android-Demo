package dev.shreyaspatil.gemini.demo.ui.screen.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.gemini.demo.ComposeActivity
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme

class AssistantActivity : ComposeActivity() {
    @Composable
    override fun RenderScreen() {
        AssistantScreen()
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AssistantScreen(
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Assistant")
            })
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth(0.8f), placeholder = {
                    Text("Ask me anything")
                })
                IconButton(
                    onClick = {},
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.onBackground,
                        CircleShape
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Send,
                        "Send",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }

            }
        }
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f))
        )
    }
}

@Preview
@Composable
fun PreviewAssistantScreen() {
    GeminiDemoTheme {
        AssistantScreen()
    }
}