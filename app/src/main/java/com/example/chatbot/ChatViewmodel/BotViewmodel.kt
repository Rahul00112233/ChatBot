package com.example.chatbot.ChatViewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.constants
import com.example.chatbot.model.chatModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class BotViewmodel: ViewModel() {

    val messageList by lazy {
        mutableStateListOf<chatModel>()
    }

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = constants.apikey,
    )

    fun sendMessage(question: String){
        viewModelScope.launch {
            val chat = generativeModel.startChat(
                history = messageList.map {
                    content(
                        role = it.role
                    ){ text(it.message)}
                }.toList()
            )


            messageList.add(chatModel(question, "user"))
            val response = chat.sendMessage(question)
            messageList.add(chatModel(response.text.toString(), "model"))
        }
    }
}