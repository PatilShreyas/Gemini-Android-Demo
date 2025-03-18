package dev.shreyaspatil.gemini.demo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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

data class Message(
    val isLoading: Boolean = false,
    val byModel: Boolean,
    val message: String,
)

@Composable
fun ChatMessage(modifier: Modifier, isLoading: Boolean, sentByUser: Boolean, message: String) {
    BoxWithConstraints(
        modifier = modifier
            .wrapContentWidth(if (sentByUser) Alignment.End else Alignment.Start)
            .background(
                if (sentByUser) SenderBackground else ReceiverBackground,
                RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
        contentAlignment = if (sentByUser) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Text(
            text = message,
            modifier = Modifier.widthIn(max = maxWidth * 0.8f),
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (isLoading) {
            CircularProgressIndicator(
                Modifier.padding(4.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun MessageList(modifier: Modifier, messages: List<Message>) {
    LazyColumn(modifier, reverseLayout = true) {
        items(messages) {
            ChatMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                isLoading = it.isLoading,
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
        ChatMessage(
            Modifier.fillMaxWidth(),
            sentByUser = true,
            isLoading = false,
            message = "Hello, how are you?"
        )
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
            isLoading = false,
            sentByUser = false,
            message = "I'm fine, thank you!"
        )
    }
}