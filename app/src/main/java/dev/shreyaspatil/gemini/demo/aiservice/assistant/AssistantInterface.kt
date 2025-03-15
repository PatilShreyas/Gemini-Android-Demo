package dev.shreyaspatil.gemini.demo.aiservice.assistant

import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.defineFunction
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

interface AssistantInterface {
    suspend fun findOnGithub(username: String): String?
    suspend fun sendSms(contactName: String, message: String): String?
    suspend fun sendWhatsAppMessage(contactName: String, message: String): String?
    suspend fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = Date()
        val formattedDate = dateFormat.format(currentDate)
        return formattedDate
    }
}

object AssistantInterfaceAdapter {
    fun getFunctions() = listOf(
        defineFunction(
            name = "findGithubUser",
            description = "Finds user details on GitHub",
            parameters = listOf(Schema.str("username", "GitHub username")),
            requiredParameters = listOf("username")
        ),
        defineFunction(
            name = "sendTextSms",
            description = "Finds user details on GitHub",
            parameters = listOf(
                Schema.str("contactName", "Name of a contact to send a message"),
                Schema.str("message", "Message content to send")
            ),
            requiredParameters = listOf("contactName", "message")
        ),
        defineFunction(
            name = "sendWhatsAppMessage",
            description = "Finds user details on GitHub",
            parameters = listOf(
                Schema.str("contactName", "Name of a contact to send a message"),
                Schema.str("message", "Message content to send")
            ),
            requiredParameters = listOf("contactName", "message")
        ),
        defineFunction(
            name = "getCurrentDateTime",
            description = "Finds current date and time",
            parameters = emptyList(),
            requiredParameters = emptyList()
        )
    )

    suspend fun invoke(functions: List<FunctionCallPart>, assistantInterface: AssistantInterface) {
        functions.forEach { function ->
            invoke(function, assistantInterface)
        }
    }

    private suspend fun invoke(
        function: FunctionCallPart,
        assistantInterface: AssistantInterface
    ): FunctionResponsePart {
        return when (function.name) {
            "findGithubUser" -> {
                val username = function.args["username"] as String
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.githubUser(assistantInterface.findOnGithub(username))
                )
            }

            "sendTextSms" -> {
                val contactName = function.args["contactName"] as String
                val message = function.args["message"] as String
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.message(assistantInterface.sendSms(contactName, message))
                )
            }

            "sendWhatsAppMessage" -> {
                val contactName = function.args["contactName"] as String
                val message = function.args["message"] as String
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.message(
                        assistantInterface.sendWhatsAppMessage(
                            contactName,
                            message
                        )
                    )
                )
            }

            "getCurrentDateTime" -> {
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.currentTime(assistantInterface.getCurrentDateTime())
                )
            }

            else -> error("Undefined function: ${function.name}")
        }
    }

    object ResponseTemplates {
        fun githubUser(details: String?) = if (details != null) JSONObject(details) else JSONObject(
            mapOf("error" to "User not found on GitHub")
        )

        fun message(status: String?) = if (status != null) {
            JSONObject(mapOf("status" to "OK"))
        } else {
            JSONObject(mapOf("status" to "failed to send sms"))
        }

        fun currentTime(details: String): JSONObject {
            return JSONObject(mapOf("time" to details))
        }
    }
}
