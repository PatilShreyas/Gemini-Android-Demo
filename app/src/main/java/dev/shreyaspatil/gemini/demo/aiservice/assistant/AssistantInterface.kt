package dev.shreyaspatil.gemini.demo.aiservice.assistant

import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.defineFunction
import org.json.JSONObject

interface AssistantInterface {
    suspend fun findOnGithub(username: String): String
    suspend fun sendSms(contactName: String, message: String): String
    suspend fun sendWhatsAppMessage(contactName: String, message: String): String
    suspend fun addToList(item: String): String
    suspend fun getAllItems(): String
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
            name = "addToTODOs",
            description = "Adds a item to the TODO list",
            parameters = listOf(
                Schema.str("item", "Item/message to be added in a TODO list"),
            ),
            requiredParameters = listOf("item")
        ),
        defineFunction(
            name = "getAllTODOItems",
            description = "Get all items from the TODO list",
            parameters = listOf(
                Schema.str("items", "Items of the todo list"),
            ),
        ),
    )

    suspend fun invoke(functions: List<FunctionCallPart>, assistantInterface: AssistantInterface): List<FunctionResponsePart> {
        return functions.map { function ->
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
                    ResponseTemplates.result(assistantInterface.findOnGithub(username))
                )
            }

            "sendTextSms" -> {
                val contactName = function.args["contactName"] as String
                val message = function.args["message"] as String
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.result(assistantInterface.sendSms(contactName, message))
                )
            }

            "sendWhatsAppMessage" -> {
                val contactName = function.args["contactName"] as String
                val message = function.args["message"] as String
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.result(
                        assistantInterface.sendWhatsAppMessage(
                            contactName,
                            message
                        )
                    )
                )
            }

            "addToTODOs" -> {
                val item = function.args["item"] as String
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.result(assistantInterface.addToList(item))
                )
            }

            "getAllTODOItems" -> {
                FunctionResponsePart(
                    function.name,
                    ResponseTemplates.result(assistantInterface.getAllItems())
                )
            }

            else -> error("Undefined function: ${function.name}")
        }
    }

    object ResponseTemplates {
        fun result(status: String) = JSONObject(mapOf("result" to status))
    }
}
