package dev.shreyaspatil.gemini.demo.aiservice

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import dev.shreyaspatil.gemini.demo.BuildConfig
import dev.shreyaspatil.gemini.demo.aiservice.assistant.AssistantInterfaceAdapter

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
        systemInstruction = content { text("Act as an assistant. You can do the following things:\n- Get current date and time\n- Send a message/SMS to the contact\n- Send a message on WhatsApp\n- Find developer's details on GitHub\n- Add the items in the TODO list/remember things\n- Get all the items/messages from TODO list/remembered things\n\n For sending the message, you can also generate a message for the user with specified context. Example: If user asks to send a birthday wishes then create a birthday wish message as per user's wish and proceed to send it") },
        tools = listOf(Tool(functionDeclarations = AssistantInterfaceAdapter.getFunctions()))
    )

    companion object {
        private const val MODEL_ID = "gemini-2.0-flash"
    }
}