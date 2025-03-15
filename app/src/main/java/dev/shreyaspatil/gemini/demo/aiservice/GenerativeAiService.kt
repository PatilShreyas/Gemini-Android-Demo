package dev.shreyaspatil.gemini.demo.aiservice

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import dev.shreyaspatil.gemini.demo.aiservice.model.CaptionResponse
import kotlinx.serialization.json.Json

class GenerativeAiService private constructor(
    private val modelRepository: GenerativeModelRepository,
) {

    suspend fun generateContent(prompt: String): String? {
        return modelRepository.getSimpleClient().generateContent(prompt).text
    }

    suspend fun generateContent(prompt: String, image: Bitmap): String? {
        return modelRepository.getSimpleClient().generateContent(
            content {
                image(image)
                text(prompt)
            }
        ).text
    }

    suspend fun generateCaption(image: Bitmap): CaptionResponse? {
        return modelRepository.getImageCaptionClient().generateContent(
            content {
                image(image)
                text("Generate a creative caption and relevant hashtags from this image for a social media post.")
            }
        ).text?.let {
            Json.decodeFromString<CaptionResponse>(it)
        }
    }

    companion object {
        val instance = GenerativeAiService(GenerativeModelRepository())
    }
}