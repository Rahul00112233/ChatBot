package com.example.chatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.chatbot.Viewmodel.BotViewmodel
import com.example.chatbot.navigation.Navigation
import com.example.chatbot.ui.theme.ChatBotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val botviewmodel = ViewModelProvider(this).get(BotViewmodel::class.java)
        setContent {
            ChatBotTheme {
                Navigation()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChatBotTheme {

    }
}