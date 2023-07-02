package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Note
import com.example.notesapp.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NoteRepositoryImp(
    val database: FirebaseFirestore
) : NoteRepository {

    override fun getNotes(): UiState<List<Note>> {
        //We will get data from firebase
        val data = arrayListOf(
            Note(
                id = "fdasf",
                text = "Note 1",
                date = Date()
            ),
            Note(
                id = "fdasf",
                text = "Note 2",
                date = Date()
            ),
            Note(
                id = "fdasf",
                text = "Note 3",
                date = Date()
            )
        )
        return if (data.isNullOrEmpty()) {
            UiState.Failure("Data is Empty")
        } else {
            UiState.Success(data)
        }
    }
}