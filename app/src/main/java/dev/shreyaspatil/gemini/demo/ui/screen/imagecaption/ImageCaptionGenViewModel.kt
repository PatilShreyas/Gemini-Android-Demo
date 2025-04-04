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
            response.value = null

            try {
                val image = attachedImage.value
                if (image != null) {
                    runCatching {
                        aiService.generateCaption(image.asAndroidBitmap())?.let {
                            response.value = ImageCaptionGenScreenUiState.Response(
                                caption = it.caption,
                                hashtags = it.hashtags.map { if (it.startsWith("#")) it else "#$it" }
                            )
                            errorMessage.value = null
                        }
                    }.getOrElse {
                        response.value = null
                        errorMessage.value =
                            "Error occurred: ${(it.message ?: "Something went wrong")}"
                        it.printStackTrace()
                    } ?: run {
                        errorMessage.value = "No response :("
                    }
                } else {
                    response.value = null
                    errorMessage.value = "Please attach an image"
                }
            } catch (e: Throwable) {
                response.value = null
                errorMessage.value = "Error occurred: ${(e.message ?: "Something went wrong")}"
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}