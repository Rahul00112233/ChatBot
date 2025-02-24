package com.example.chatbot.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isPinned: Boolean = false,
    val color: String = "#FFFFFF",
    val label: String? = null
)
