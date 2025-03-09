package dev.shreyaspatil.gemini.demo.aiservice

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content

class GenerativeAiService private constructor(
    private val modelRepository: GenerativeModelRepository,
) {

    suspend fun generateContent(prompt: String): GenerateContentResponse {
        return modelRepository.getSimpleClient().generateContent(prompt)
    }

    suspend fun generateContent(prompt: String, image: Bitmap): GenerateContentResponse {
        return modelRepository.getSimpleClient().generateContent(
            content {
                image(image)
                text(prompt)
            }
        )
    }

    fun startChat(history: List<Content>) = modelRepository.getSimpleClient().startChat(history)

    companion object {
        val instance = GenerativeAiService(GenerativeModelRepository())
    }
}