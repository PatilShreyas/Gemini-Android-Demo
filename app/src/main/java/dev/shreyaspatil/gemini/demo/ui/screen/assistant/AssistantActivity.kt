package dev.shreyaspatil.gemini.demo.ui.screen.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shreyaspatil.gemini.demo.ComposeActivity
import dev.shreyaspatil.gemini.demo.ui.components.Message
import dev.shreyaspatil.gemini.demo.ui.components.MessageList
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme

class AssistantActivity : ComposeActivity() {
    @Composable
    override fun RenderScreen() {
        val viewModel = viewModel<AssistantViewModel>()
        val messages by viewModel.messages.collectAsStateWithLifecycle()
        AssistantScreen(messages, onSendMessage = viewModel::sendMessage)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(messages: List<Message>, onSendMessage: (String) -> Unit = {}) {
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
                    .padding(vertical = 8.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                var message by remember { mutableStateOf("") }
                OutlinedTextField(
                    message,
                    { message = it },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    placeholder = {
                        if (message.isBlank()) {
                            Text("Ask me anything")
                        }
                    }
                )
                IconButton(
                    onClick = {
                        onSendMessage(message)
                        message = ""
                    },
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
        MessageList(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .padding(16.dp)
                .imePadding(),
            messages = remember(messages) { messages.reversed() }
        )
    }
}

@Preview
@Composable
fun PreviewAssistantScreen() {
    GeminiDemoTheme {
        val messages = listOf(
            Message(
                isLoading = false,
                byModel = true,
                message = "Hello, how are you? I'm an AI assistant and I can help you with variety of tasks."
            ),
            Message(isLoading = false, byModel = false, message = "I'm fine, thank you!"),
            Message(isLoading = false, byModel = true, message = "What about you?"),
            Message(
                isLoading = false,
                byModel = false,
                message = "I'm good too, thanks for asking!"
            ),
            Message(
                isLoading = true,
                byModel = true,
                message = ""
            )
        )
        AssistantScreen(messages)
    }
}