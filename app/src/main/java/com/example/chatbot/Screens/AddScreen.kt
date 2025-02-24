package com.example.chatbot.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatbot.Viewmodel.NoteViewModel
import com.example.chatbot.model.NoteEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(viewModel: NoteViewModel, navController: NavController, noteId: Int) {
    val noteEntity by viewModel.getNoteById(noteId).observeAsState()
    var title by remember { mutableStateOf(noteEntity?.title ?: "") }
    var content by remember { mutableStateOf(noteEntity?.content ?: "") }
    var selectedColor by remember { mutableStateOf(noteEntity?.color ?: "#FFFFFF") }
    var label by remember { mutableStateOf(noteEntity?.label ?: "") }
    var showDeleteMenu by remember { mutableStateOf(false) }
    var summary by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val colors = listOf("#FFFFFF", "#FFCDD2", "#C8E6C9", "#BBDEFB", "#FFECB3", "#D1C4E9")
    LaunchedEffect(noteEntity) {
        noteEntity?.let {
            title = it.title
            content = it.content
            selectedColor = it.color
            label = it.label!!
            viewModel.startEditing(it)
        }
    }
    fun autoSave() {
        if (noteId == 0) {
            viewModel.addNote(title, content, selectedColor, label)
        } else {
            viewModel.updateNoteContent(title, content, selectedColor, label)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(android.graphics.Color.parseColor(selectedColor))
            )
    )
    {
        Column(modifier = Modifier.padding(vertical = 48.dp, horizontal = 8.dp).verticalScroll(
            rememberScrollState()
        )) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
                }
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        isGenerating = true
                        viewModel.generateNoteSummary(content) { result ->
                            summary = result
                            isGenerating = false
                        }
                    },
                        modifier = Modifier.scale(0.8f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Auto Summarize")
                    }
                    IconButton(onClick = {
                        noteEntity?.let { viewModel.pinNote(it) }
                    }) {
                        Icon(imageVector = Icons.Outlined.Star, contentDescription = null)
                    }
                    IconButton(onClick = { showDeleteMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                    }

                    DropdownMenu(
                        expanded = showDeleteMenu,
                        onDismissRequest = { showDeleteMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                noteEntity?.let {
                                    viewModel.deleteNote(it)
                                    navController.popBackStack()
                                }
                                showDeleteMenu = false
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                Color(android.graphics.Color.parseColor(color)),
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = color
                                autoSave()
                            }
                            .border(
                                width = if (selectedColor == color) 2.dp else 0.dp,
                                color = if (selectedColor == color) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    autoSave()
                },
                placeholder = {
                    Text(
                        text = "Title",
                        fontSize = 26.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
                    .heightIn(min = 64.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 26.sp
                ),
                shape = RoundedCornerShape(8.dp)
            )
            if (summary.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
                ) {
                    val formattedMessage = parseMarkdown(summary)
                    SelectionContainer {
                        Text(
                            text = formattedMessage,
                            fontSize = 18.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    autoSave()
                },
                placeholder = { Text("Write here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
                    .padding(bottom = 16.dp)
                    .pointerInput(Unit){
                        detectTapGestures(
                            onLongPress = {offset ->
                                val selection = content
                            }
                        )
                    },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = Int.MAX_VALUE
            )
        }
    }
}

@Composable
fun parseMarkdown(message: String): AnnotatedString {
    return buildAnnotatedString {
        var tempMessage = message

        val headerRegex = Regex("^(#{1,6})\\s+(.*)")
        val headerMatch = headerRegex.find(tempMessage)
        if (headerMatch != null) {
            val level = headerMatch.groupValues[1].length
            val headerText = headerMatch.groupValues[2]
            append(headerText)
            addStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = (22 - (level * 2)).sp
                ), 0, headerText.length
            )
            return@buildAnnotatedString
        }

        val boldItalicRegex = Regex("(\\*\\*.*?\\*\\*|\\*.*?\\*)")
        var lastIndex = 0

        boldItalicRegex.findAll(tempMessage).forEach { match ->
            val start = match.range.first
            val end = match.range.last + 1

            append(tempMessage.substring(lastIndex, start))

            val matchedText = match.value
            when {
                matchedText.startsWith("**") -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(matchedText.removeSurrounding("**"))
                    pop()
                }
                matchedText.startsWith("*") -> {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(matchedText.removeSurrounding("*"))
                    pop()
                }
            }
            lastIndex = end
        }

        if (lastIndex < tempMessage.length) {
            append(tempMessage.substring(lastIndex))
        }
    }
}
