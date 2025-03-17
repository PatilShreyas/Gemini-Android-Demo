package dev.shreyaspatil.gemini.demo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.core.net.toUri
import dev.shreyaspatil.gemini.demo.aiservice.assistant.AssistantInterface
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class AssistantService(
    private val context: Context,
    private val httpClient: HttpClient = HttpClient()
) : AssistantInterface {

    private val todoItems = mutableListOf<String>()

    override suspend fun findOnGithub(username: String): String {
        val response = httpClient.get("https://api.github.com/users/$username")
        return when (response.status) {
            HttpStatusCode.OK -> "User found on GitHub, here is data in JSON format: ${response.bodyAsText()}"
            HttpStatusCode.NotFound -> "User not found on GitHub."
            else -> "Failed to find user on GitHub."
        }
    }

    override suspend fun sendSms(contactName: String, message: String): String {
        val phoneNumber = findPhoneNumberByName(contactName) ?: return contactNotFound(contactName)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            data = "smsto:$phoneNumber".toUri()
            putExtra("sms_body", message)
        }
        context.startActivity(intent)
        return "SMS app will be launched to send message to $contactName at $phoneNumber"
    }

    override suspend fun sendWhatsAppMessage(contactName: String, message: String): String {
        val phoneNumber = findPhoneNumberByName(contactName) ?: return contactNotFound(contactName)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            data = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}".toUri()
        }
        context.startActivity(intent)
        return "WhatsApp will be launched to send message to $contactName at $phoneNumber"
    }

    override suspend fun addToList(item: String): String {
        todoItems.add(item)
        return "Added item to the TODO list: $item"
    }

    override suspend fun getAllItems(): String {
        if (todoItems.isEmpty()) return "No items in the TODO list"
        return todoItems.joinToString("\n") { "- $it" }
    }

    private fun findPhoneNumberByName(contactName: String): String? {
        val contentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?",
            arrayOf(contactName),
            null
        )

        return cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (index == -1) return@use null
                val phoneNumber = it.getString(index)
                return@use phoneNumber
            }
            return@use null
        }
    }

    private fun contactNotFound(name: String) = "Contact with name $name not found in contacts."

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        fun init(context: Context) {
            this.context = context.applicationContext
        }

        val instance: AssistantService by lazy { AssistantService(context!!) }
    }
}