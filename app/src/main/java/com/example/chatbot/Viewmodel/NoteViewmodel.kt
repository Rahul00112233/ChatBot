package com.example.chatbot.Viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatbot.model.NoteDatabase
import com.example.chatbot.model.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    val allNotes: LiveData<List<NoteEntity>> = noteDao.getAllNotes().asLiveData()

    private val _currentNote = MutableStateFlow<NoteEntity?>(null)
    val currentNote: StateFlow<NoteEntity?> = _currentNote

    private var autoSaveJob: Job? = null

    fun addNote(title: String, content: String, color: String, label: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_currentNote.value == null || _currentNote.value?.id == 0) {
                val note = NoteEntity(
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis(),
                    color = color,
                    label = label
                )
                val noteId = noteDao.insertNote(note).toInt()
                _currentNote.value = note.copy(id = noteId)
            } else {
                _currentNote.value?.let { note ->
                    val updatedNote = note.copy(
                        title = title,
                        content = content,
                        timestamp = System.currentTimeMillis(),
                        color = color,
                        label = label
                    )
                    noteDao.updateNote(updatedNote)
                    _currentNote.value = updatedNote
                }
            }
        }
    }


    fun getNoteById(noteId: Int): LiveData<NoteEntity?> {
        return if (noteId == 0) MutableLiveData(null) else noteDao.getNoteById(noteId).asLiveData()
    }

    fun startEditing(note: NoteEntity) {
        viewModelScope.launch {
            _currentNote.emit(note)
        }
    }

    fun updateNoteContent(title: String, content: String, color: String, label: String?) {
        _currentNote.value?.let { note ->
            _currentNote.value = note.copy(
                title = title,
                content = content,
                timestamp = System.currentTimeMillis(),
                color = color,
                label = label
            )
            scheduleAutoSave()
        }
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(1500)
            _currentNote.value?.let { note ->
                if (note.id == 0) {
                    val noteId = noteDao.insertNote(note).toInt()
                    _currentNote.value = note.copy(id = noteId)
                } else {
                    noteDao.updateNote(note)
                }
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNote(note)
        }
    }
    fun generateNoteSummary(noteContent: String, onSummaryGenerated: (String) -> Unit) {
        viewModelScope.launch {
            val summary = BotViewmodel.Gemini.getSummary(noteContent)
            onSummaryGenerated(summary)
        }
    }

    fun pinNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }
}
