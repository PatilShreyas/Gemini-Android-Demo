package dev.shreyaspatil.gemini.demo.ui.screen.simpleprompt

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shreyaspatil.gemini.demo.aiservice.GenerativeAiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class SimpleScreenUiState(
    val isLoading: Boolean = false,
    val prompt: String = "",
    val attachedImage: ImageBitmap? = null,
    val response: String = "",
    val isError: Boolean = false,
)

class SimplePromptViewModel(
    private val aiService: GenerativeAiService = GenerativeAiService.instance,
) : ViewModel() {
    private val prompt = MutableStateFlow("")
    private val response = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)
    private val isError = MutableStateFlow(false)
    private val attachedImage = MutableStateFlow<ImageBitmap?>(null)

    val state = combine(
        prompt,
        response,
        isLoading,
        isError,
        attachedImage
    ) { prompt, response, isLoading, isError, attachedImage ->
        SimpleScreenUiState(
            isLoading = isLoading,
            prompt = prompt,
            response = response,
            isError = isError,
            attachedImage = attachedImage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SimpleScreenUiState()
    )

    fun onPromptChange(prompt: String) {
        this.prompt.value = prompt
    }

    fun onImageAttached(image: ImageBitmap?) {
        attachedImage.value = image
    }

    fun generateResponse() {
        viewModelScope.launch {
            isLoading.value = true
            response.value = ""

            try {
                val image = attachedImage.value
                val aiResponse = if (image != null) {
                    aiService.generateContent(prompt.value, image.asAndroidBitmap())
                } else {
                    aiService.generateContent(prompt.value)
                }
                response.value = aiResponse.text ?: ""
                isError.value = false
            } catch (e: Throwable) {
                isError.value = true
                response.value = "Error occurred: ${(e.message ?: "Something went wrong")}"
            } finally {
                isLoading.value = false
            }
        }
    }
}