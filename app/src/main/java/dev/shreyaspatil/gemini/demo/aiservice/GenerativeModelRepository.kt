package dev.shreyaspatil.gemini.demo.aiservice

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.generationConfig
import dev.shreyaspatil.gemini.demo.BuildConfig

class GenerativeModelRepository {
    fun getSimpleClient() = GenerativeModel(
        modelName = MODEL_ID,
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.9f
        },
    )

    fun getImageCaptionClient() = GenerativeModel(
        modelName = MODEL_ID,
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.75f
            responseSchema = Schema.obj(
                name = "response",
                description = "The response object",
                Schema.obj("caption", "The caption for the social media post"),
                Schema.arr("hashtags", "Hash tags for the social media post")
            )
        },
    )

    companion object {
        private const val MODEL_ID = "gemini-2.0-flash"
    }
}