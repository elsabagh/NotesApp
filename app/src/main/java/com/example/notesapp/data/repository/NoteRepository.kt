package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Note
import com.example.notesapp.util.UiState

interface NoteRepository {

    fun getNotes(): UiState<List<Note>>
}