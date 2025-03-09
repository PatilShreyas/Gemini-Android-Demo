package dev.shreyaspatil.gemini.demo.ui.screen.imagecaption

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shreyaspatil.gemini.demo.ComposeActivity
import dev.shreyaspatil.gemini.demo.ui.components.ImagePicker
import dev.shreyaspatil.gemini.demo.ui.components.dashedBorder
import dev.shreyaspatil.gemini.demo.ui.theme.ErrorBackground
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme
import dev.shreyaspatil.gemini.demo.ui.theme.SuccessBackground

class ImageCaptionGenActivity : ComposeActivity() {
    @Composable
    override fun RenderScreen() {
        val viewModel = viewModel<ImageCaptionGenViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        SimplePromptScreen(
            state = state.value,
            onGenerateButtonClick = viewModel::generateResponse,
            onImageAttached = viewModel::onImageAttached
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePromptScreen(
    state: ImageCaptionGenScreenUiState,
    onGenerateButtonClick: () -> Unit = {},
    onImageAttached: (ImageBitmap?) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Simple Prompt")
            })
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AttachImageArea(
                Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                attachedImage = state.attachedImage,
                onImageAttached = onImageAttached
            )

            Button(
                onClick = onGenerateButtonClick,
                enabled = !state.isLoading
            ) {
                Text("Generate Caption")
                if (state.isLoading) {
                    CircularProgressIndicator(
                        Modifier
                            .padding(start = 8.dp)
                            .size(16.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            HorizontalDivider()

            val cardBackground = when {
                state.errorMessage != null -> CardDefaults.cardColors(containerColor = ErrorBackground)
                state.response != null -> CardDefaults.cardColors(containerColor = SuccessBackground)
                else -> CardDefaults.cardColors()
            }

            Card(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(top = 4.dp),
                colors = cardBackground
            ) {
                val response = state.response
                if (response == null) {
                    Text("Caption will appear here", modifier = Modifier.padding(8.dp))
                } else {
                    Text(
                        text = state.response.toString(),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AttachImageArea(
    modifier: Modifier = Modifier,
    attachedImage: ImageBitmap? = null,
    onImageAttached: (ImageBitmap?) -> Unit = {},
) {
    Box(
        modifier
            .background(Color.White.copy(alpha = 0.1f))
            .dashedBorder(2.dp, Color.White, 10.dp, 10.dp, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        ImagePicker(Modifier.size(100.dp), onImagePicked = onImageAttached)

        attachedImage?.let { image ->
            Image(
                bitmap = image,
                contentDescription = "Attached image",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Icon(
                Icons.Default.Clear,
                "Clear attached image",
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .padding(12.dp)
                    .clickable { onImageAttached(null) },
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SimplePromptScreenPreview_empty() {
    GeminiDemoTheme {
        SimplePromptScreen(ImageCaptionGenScreenUiState())
    }
}


@Preview(showBackground = true)
@Composable
fun SimplePromptScreenPreview_response() {
    GeminiDemoTheme {
        SimplePromptScreen(
            ImageCaptionGenScreenUiState(
                response = ImageCaptionGenScreenUiState.Response(
                    """
                        Solara vesta quintonis meridia, tempora fluctuatis.
                   """.trimIndent(),
                    listOf("#trending", "#wow", "#android", "#life")
                )
            )
        )
    }
}
