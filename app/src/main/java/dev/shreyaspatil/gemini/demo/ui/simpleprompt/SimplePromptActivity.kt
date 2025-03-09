package dev.shreyaspatil.gemini.demo.ui.simpleprompt

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shreyaspatil.gemini.demo.ComposeActivity
import dev.shreyaspatil.gemini.demo.ui.theme.ErrorBackground
import dev.shreyaspatil.gemini.demo.ui.theme.GeminiDemoTheme
import dev.shreyaspatil.gemini.demo.ui.theme.SuccessBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SimplePromptActivity : ComposeActivity() {
    @Composable
    override fun RenderScreen() {
        val viewModel = viewModel<SimplePromptViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        SimplePromptScreen(
            state = state.value,
            onPromptChange = viewModel::onPromptChange,
            onGenerateButtonClick = viewModel::generateResponse,
            onImageAttached = viewModel::onImageAttached
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePromptScreen(
    state: SimpleScreenUiState,
    onPromptChange: (String) -> Unit = {},
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
            OutlinedTextField(
                value = state.prompt,
                onValueChange = onPromptChange,
                maxLines = 5,
                placeholder = {
                    if (state.prompt.isBlank()) {
                        Text(text = "Write your prompt")
                    }
                },
                leadingIcon = {
                    ImagePicker(onImagePicked = onImageAttached)
                },
                trailingIcon = {
                    if (state.prompt.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear prompt",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { onPromptChange("") },
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { onGenerateButtonClick() }),
                modifier = Modifier.fillMaxWidth()
            )

            state.attachedImage?.let { image ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        bitmap = image,
                        contentDescription = "Attached image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        Icons.Default.Clear,
                        "Clear attached image",
                        Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .padding(4.dp)
                            .clickable { onImageAttached(null) },
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            Button(
                onClick = onGenerateButtonClick,
                enabled = !state.isLoading && state.prompt.isNotBlank()
            ) {
                Text("Generate response")
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
                state.isError -> CardDefaults.cardColors(containerColor = ErrorBackground)
                state.response.isNotBlank() -> CardDefaults.cardColors(containerColor = SuccessBackground)
                else -> CardDefaults.cardColors()
            }

            Card(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(top = 4.dp),
                colors = cardBackground
            ) {
                if (state.response.isEmpty()) {
                    Text("Response will appear here", modifier = Modifier.padding(8.dp))
                } else {
                    Text(
                        text = state.response,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ImagePicker(onImagePicked: (ImageBitmap) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            val imageUri = uri ?: run {
                Toast.makeText(context, "Image not attached", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            scope.launch(Dispatchers.Default) {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                }
                bitmap?.let {
                    onImagePicked(it.asImageBitmap())
                }
            }
        }

    Icon(
        Icons.Filled.Image,
        contentDescription = "Attach Image",
        Modifier
            .padding(4.dp)
            .clickable(onClick = {
                launcher.launch("image/*")
            }),
    )
}

@Preview(showBackground = true)
@Composable
fun SimplePromptScreenPreview_empty() {
    GeminiDemoTheme {
        SimplePromptScreen(SimpleScreenUiState())
    }
}

@Preview(showBackground = true)
@Composable
fun SimplePromptScreenPreview_prompt() {
    GeminiDemoTheme {
        SimplePromptScreen(SimpleScreenUiState(prompt = "Lorem Ipsum"))
    }
}

@Preview(showBackground = true)
@Composable
fun SimplePromptScreenPreview_response() {
    GeminiDemoTheme {
        SimplePromptScreen(
            SimpleScreenUiState(
                prompt = "Lorem Ipsum",
                response = """
                        Solara vesta quintonis meridia, tempora fluctuatis.
                        Caelum umbra voxilis, lumina astralis gravitas.
                        Fluentia nexus temporalis, veridian nexus inceptum.
                        Orbis structura silentia, aetheria claritas resonata.
                        Zenithia motus temporis, cardinalis forma lumina.
                   """.trimIndent()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimplePromptScreenPreview_loading() {
    GeminiDemoTheme {
        SimplePromptScreen(
            SimpleScreenUiState(
                prompt = "Lorem Ipsum",
                isLoading = true
            )
        )
    }
}