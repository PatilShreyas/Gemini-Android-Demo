package dev.shreyaspatil.gemini.demo.aiservice

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
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
            responseMimeType = "application/json"
            responseSchema = Schema.obj(
                name = "response",
                description = "The response object",
                Schema.str("caption", "The caption for the social media post without hashtags"),
                Schema.arr(
                    name = "hashtags",
                    description = "List of hashtags relevant for the post",
                    items = Schema.str("hashtag", "hashtag")
                )
            )
        },
    )

    fun getAssistantChatClient() = GenerativeModel(
        "gemini-2.0-flash",
        BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.95f
        },
        systemInstruction = content { text("Act as an assistant. You can do the following things:\n- Get current date and time\n- Send a message/SMS to the contact\n- Send a message on WhatsApp\n- Find developer's details on GitHub") },
        tools = listOf(Tool(functionDeclarations = listOf()))
    )

    companion object {
        private const val MODEL_ID = "gemini-2.0-flash"
    }
}