package com.example.notesapp.data.repository

import android.net.Uri
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.User
import com.example.notesapp.util.UiState

interface NoteRepository {

    fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit)
    fun addNote(note: Note, result: (UiState<Pair<Note, String>>) -> Unit)

    fun updateNote(note: Note, result: (UiState<String>) -> Unit)
    fun deleteNote(note: Note, result: (UiState<String>) -> Unit)
    suspend fun uploadFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    fun deleteImage(note: Note, imageUrl: String, result: (UiState<String>) -> Unit)

}