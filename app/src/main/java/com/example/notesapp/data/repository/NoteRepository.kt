package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Note
import com.example.notesapp.util.UiState

interface NoteRepository {

    fun getNotes(result: (UiState<List<Note>>) -> Unit)
    fun addNote(note: Note, result: (UiState<String>) -> Unit)
}