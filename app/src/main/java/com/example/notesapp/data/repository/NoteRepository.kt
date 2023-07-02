package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Note

interface NoteRepository {

    fun getNotes(): List<Note>
}