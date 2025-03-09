package dev.shreyaspatil.gemini.demo.aiservice

import com.google.ai.client.generativeai.GenerativeModel
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

    companion object {
        private const val MODEL_ID = "gemini-2.0-flash"
    }
}