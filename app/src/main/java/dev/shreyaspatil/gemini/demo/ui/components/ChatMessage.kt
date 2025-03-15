package dev.shreyaspatil.gemini.demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme
import dev.shreyaspatil.gemini.demo.ui.theme.ReceiverBackground
import dev.shreyaspatil.gemini.demo.ui.theme.SenderBackground

data class Message(val byModel: Boolean, val message: String)

@Composable
fun ChatMessage(modifier: Modifier, sentByUser: Boolean, message: String) {
    Box(
        modifier = modifier,
        contentAlignment = if (sentByUser) Alignment.CenterEnd else Alignment.CenterStart,
    ) {

        Text(
            text = message,
            modifier = Modifier
                .wrapContentWidth(if (sentByUser) Alignment.End else Alignment.Start)
                .background(
                    if (sentByUser) SenderBackground else ReceiverBackground,
                    RoundedCornerShape(4.dp)
                )
                .padding(8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun MessageList(modifier: Modifier, messages: List<Message>) {
    LazyColumn(modifier) {
        items(messages) {
            ChatMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                sentByUser = !it.byModel,
                message = it.message
            )
        }
    }
}

@Preview
@Composable
fun ChatMessagePreview_sent() {
    GeminiDemoTheme {
        ChatMessage(Modifier.fillMaxWidth(), sentByUser = true, message = "Hello, how are you?")
    }
}

@Preview
@Composable
fun ChatMessagePreview_received() {
    GeminiDemoTheme {
        ChatMessage(
            Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            sentByUser = false,
            message = "I'm fine, thank you!"
        )
    }
}

@Preview
@Composable
fun MessageListPreview() {
    GeminiDemoTheme {
        val messages = listOf(
            Message(true, "Hello, how are you?"),
            Message(false, "I'm fine, thank you!"),
            Message(true, "What about you?"),
            Message(false, "I'm good too, thanks for asking!")
        )
        MessageList(Modifier.fillMaxSize(), messages)
    }
}