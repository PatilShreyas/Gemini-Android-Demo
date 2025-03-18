package dev.shreyaspatil.gemini.demo.aiservice.assistant

import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.defineFunction
import org.json.JSONObject

/**
 * Assistant interface for performing the tasks in the device
 */
interface AssistantInterface {
    /**
     * Finds user details on GitHub
     */
    suspend fun findOnGithub(username: String): String

    /**
     * Sends a text message to a contact
     */
    suspend fun sendSms(contactName: String, message: String): String

    /**
     * Sends a WhatsApp message to a contact
     */
    suspend fun sendWhatsAppMessage(contactName: String, message: String): String

    /**
     * Adds an item to the TODO list
     */
    suspend fun addToList(item: String): String

    /**
     * Get all items from the TODO list
     */
    suspend fun getAllItems(): String
}

/**
 * Adapter for Assistant Interface to invoke functions or get the info about functions
 */
object AssistantInterfaceAdapter {
    /**
     * Get all functions available in the Assistant Interface
     */
    fun getFunctions() = listOf(
        defineFunction(
            name = FunctionNames.FIND_GITHUB_USER,
            description = "Finds user details on GitHub",
            parameters = listOf(Schema.str("username", "GitHub username")),
            requiredParameters = listOf("username")
        ),
        defineFunction(
            name = FunctionNames.SEND_TEXT_SMS,
            description = "Sends a text message to a contact via SMS",
            parameters = listOf(
                Schema.str("contactName", "Name of a contact to send a message"),
                Schema.str("message", "Message content to send")
            ),
            requiredParameters = listOf("contactName", "message")
        ),
        defineFunction(
            name = FunctionNames.SEND_WHATSAPP_MESSAGE,
            description = "Sends a message to a contact via WhatsApp",
            parameters = listOf(
                Schema.str("contactName", "Name of a contact to send a message"),
                Schema.str("message", "Message content to send")
            ),
            requiredParameters = listOf("contactName", "message")
        ),
        defineFunction(
            name = FunctionNames.ADD_TO_TODOS,
            description = "Adds a item to the TODO list",
            parameters = listOf(
                Schema.str("item", "Item/message to be added in a TODO list"),
            ),
            requiredParameters = listOf("item")
        ),
        defineFunction(
            name = FunctionNames.GET_ALL_TODOS,
            description = "Get all items from the TODO list",
            parameters = listOf(
                Schema.str("items", "Items of the todo list"),
            ),
        ),
    )

    /**
     * Invoke functions with the given arguments
     */
    suspend fun invoke(
        functions: List<FunctionCallPart>,
        assistantInterface: AssistantInterface
    ): List<FunctionResponsePart> {
        return functions.map { function ->
            invoke(function, assistantInterface)
        }
    }

    private suspend fun invoke(
        function: FunctionCallPart,
        assistantInterface: AssistantInterface
    ): FunctionResponsePart {
        return when (function.name) {
            FunctionNames.FIND_GITHUB_USER -> {
                val username = function.args["username"] as String
                FunctionResponsePart(
                    function.name,
                    response(assistantInterface.findOnGithub(username))
                )
            }

            FunctionNames.SEND_TEXT_SMS -> {
                val contactName = function.args["contactName"] as String
                val message = function.args["message"] as String
                FunctionResponsePart(
                    function.name,
                    response(assistantInterface.sendSms(contactName, message))
                )
            }

            FunctionNames.SEND_WHATSAPP_MESSAGE -> {
                val contactName = function.args["contactName"] as String
                val message = function.args["message"] as String
                FunctionResponsePart(
                    function.name,
                    response(assistantInterface.sendWhatsAppMessage(contactName, message))
                )
            }

            FunctionNames.ADD_TO_TODOS -> {
                val item = function.args["item"] as String
                FunctionResponsePart(
                    function.name,
                    response(assistantInterface.addToList(item))
                )
            }

            FunctionNames.GET_ALL_TODOS -> {
                FunctionResponsePart(
                    function.name,
                    response(assistantInterface.getAllItems())
                )
            }

            else -> error("Undefined function: ${function.name}")
        }
    }

    private fun response(status: String) = JSONObject(mapOf("result" to status))

    object FunctionNames {
        const val FIND_GITHUB_USER = "findGithubUser"
        const val SEND_TEXT_SMS = "sendTextSms"
        const val SEND_WHATSAPP_MESSAGE = "sendWhatsAppMessage"
        const val ADD_TO_TODOS = "addToTODOs"
        const val GET_ALL_TODOS = "getAllTODOItems"
    }
}
