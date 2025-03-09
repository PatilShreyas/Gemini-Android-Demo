package dev.shreyaspatil.gemini.demo.ui.screen.imagecaption

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

data class ImageCaptionGenScreenUiState(
    val isLoading: Boolean = false,
    val attachedImage: ImageBitmap? = null,
    val response: Response? = null,
    val errorMessage: String? = null,
) {
    data class Response(val caption: String, val hashtags: List<String>)
}

class ImageCaptionGenViewModel(
    private val aiService: GenerativeAiService = GenerativeAiService.instance,
) : ViewModel() {
    private val prompt = MutableStateFlow("")
    private val response = MutableStateFlow<ImageCaptionGenScreenUiState.Response?>(null)
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val attachedImage = MutableStateFlow<ImageBitmap?>(null)

    val state = combine(
        response,
        isLoading,
        errorMessage,
        attachedImage
    ) { response, isLoading, error, attachedImage ->
        ImageCaptionGenScreenUiState(
            isLoading = isLoading,
            response = response,
            errorMessage = error,
            attachedImage = attachedImage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImageCaptionGenScreenUiState()
    )

    fun onImageAttached(image: ImageBitmap?) {
        attachedImage.value = image
    }

    fun generateResponse() {
        viewModelScope.launch {
            isLoading.value = true
//            response.value = ""

            try {
                val image = attachedImage.value
                if (image != null) {
                    val aiResponse = aiService.generateCaption(image.asAndroidBitmap())
                    response.value =
                        ImageCaptionGenScreenUiState.Response(aiResponse.text!!, emptyList())
                    errorMessage.value = null
                } else {
                    errorMessage.value = "Please attach an image"
                }
            } catch (e: Throwable) {
                errorMessage.value = "Error occurred: ${(e.message ?: "Something went wrong")}"
            } finally {
                isLoading.value = false
            }
        }
    }
}