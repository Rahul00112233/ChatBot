package com.example.chatbot.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatbot.ChatViewmodel.BotViewmodel
import com.example.chatbot.model.chatModel
import com.example.chatbot.ui.theme.Color1
import com.example.chatbot.ui.theme.Color2
import com.example.chatbot.ui.theme.headerColor

@Composable
fun HomeScreen(viewmodel: BotViewmodel) {

    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(color = headerColor),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = "CHAT BOT",
                fontSize = 26.sp,
                modifier = Modifier.padding(12.dp),
                color = Color.White
            )
        }

        Column(modifier = Modifier
            .weight(1f)
            .padding(12.dp)){
            MessageList(
                messageList = viewmodel.messageList,
                modifier = Modifier.padding(16.dp)
            )
        }


        Column(modifier = Modifier.fillMaxWidth()){
            OutlinedTextField(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                value = text,
                onValueChange = { text = it },
                trailingIcon = {
                    IconButton(onClick = {
                        viewmodel.sendMessage(question = text)
                        text = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = headerColor
                        )
                    }
                },
                shape = RoundedCornerShape(32.dp),
                placeholder = {
                    Text(text = "Ask me anything...")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = headerColor,
                    unfocusedBorderColor = headerColor,
                )
            )
        }
    }
}

@Composable
fun MessageList(messageList: List<chatModel>, modifier: Modifier) {
    LazyColumn(
        reverseLayout = true
    ){
        items(messageList.reversed()) {
            MessageRow(message = it)
        }
    }
}

@Composable
fun MessageRow(message: chatModel) {
    val isModel = message.role == "model"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (!isModel) 16.dp else 0.dp, end = if (isModel) 16.dp else 0.dp, top = 8.dp),
        horizontalArrangement = if (!isModel) Arrangement.End else Arrangement.Start
    ){
        Box(modifier = Modifier
            .background(
                color = if (isModel) Color2 else Color1,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
        ) {
            SelectionContainer {
                Text(text = message.message, color = Color.White)
            }

        }
    }
}

