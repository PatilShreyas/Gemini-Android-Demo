package dev.shreyaspatil.gemini.demo.ui.screen.assistant

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import dev.shreyaspatil.gemini.demo.AssistantService
import dev.shreyaspatil.gemini.demo.aiservice.GenerativeAiService
import dev.shreyaspatil.gemini.demo.aiservice.assistant.AssistantInterface
import dev.shreyaspatil.gemini.demo.aiservice.assistant.AssistantInterfaceAdapter
import dev.shreyaspatil.gemini.demo.ui.components.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class AssistantUiScreenState(
    val messages: List<Message>,
)

class AssistantViewModel(
    private val aiService: GenerativeAiService = GenerativeAiService.instance,
    private val assistantInterface: AssistantInterface = AssistantService.instance
) : ViewModel() {
    private val _messages = MutableStateFlow(listOf<Message>())
    val messages = _messages.asStateFlow()

    private val chat = aiService.startAssistantChat()

    fun sendMessage(message: String) {
        viewModelScope.launch {
            addUserMessage(message)
            addModelLoadingResponse()

            val response = chat.sendMessage(message)
            addModelFinalResponse(response.proceedToFunctionCallIfRequired().text ?: "")
        }
    }

    /**
     * Proceeds to function call if required.
     *
     * @return [GenerateContentResponse] after proceeding to function call if required.
     */
    private suspend fun GenerateContentResponse.proceedToFunctionCallIfRequired(): GenerateContentResponse {
        val functionResponses = AssistantInterfaceAdapter.invoke(functionCalls, assistantInterface)
        return if (functionResponses.isNotEmpty()) {
            chat.sendMessage(content("function") {
                parts.addAll(functionResponses)
            }).proceedToFunctionCallIfRequired()
        } else {
            return this
        }
    }

    private fun addUserMessage(message: String) {
        _messages.update { it + Message(byModel = false, message = message) }
    }

    private fun addModelLoadingResponse() {
        _messages.update { it + Message(byModel = true, message = "", isLoading = true) }
    }

    private fun addModelFinalResponse(message: String = "") {
        _messages.update { it.dropLast(1) + Message(byModel = true, message = message) }
    }
}