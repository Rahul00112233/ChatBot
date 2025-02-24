package com.example.chatbot.Viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.constants
import com.example.chatbot.model.chatModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BotViewmodel: ViewModel() {

    private val _messageList = mutableStateListOf<chatModel>()
    val messageList: List<chatModel> = _messageList
    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = constants.apikey
    )



    object Gemini {

        suspend fun getSummary(noteContent: String): String {
            return withContext(Dispatchers.IO) {
                try {
                    val model = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = constants.apikey
                    )
                    val prompt = """
    Summarize the following note in a clear and concise manner, preserving key details. 
    The summary should be structured, avoiding unnecessary details, and should be no longer than 3-4 sentences. 
    If the note contains actionable tasks, highlight them separately. Use emojis where necessary like for bullet points.
    
    Note Content: "$noteContent"
""".trimIndent()

                    val response = model.generateContent(prompt)
                    response.text ?: "Could not generate summary."
                } catch (e: Exception) {
                    Log.e("GeminiHelper", "Error generating summary: ${e.message}")
                    "Error generating summary."
                }
            }
        }
    }
}