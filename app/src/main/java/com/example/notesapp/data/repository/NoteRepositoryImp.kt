package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.User
import com.example.notesapp.util.FireStoreDocumentField
import com.example.notesapp.util.FireStoreTAbles
import com.example.notesapp.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class NoteRepositoryImp(
    val database: FirebaseFirestore
) : NoteRepository {

    override fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit) {
        database.collection(FireStoreTAbles.Note)
            .whereEqualTo(FireStoreDocumentField.USER_ID, user?.id)
            .orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Note>()
                for (document in it) {
                    val note = document.toObject(Note::class.java)
                    notes.add(note)
                }
                result.invoke(
                    UiState.Success(notes)
                )

            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun addNote(note: Note, result: (UiState<Pair<Note, String>>) -> Unit) {
        val document = database.collection(FireStoreTAbles.Note).document()
        note.id = document.id

        document.set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(note, "Note has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun updateNote(note: Note, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreTAbles.Note).document(note.id)
        document.set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun deleteNote(note: Note, result: (UiState<String>) -> Unit) {
        database.collection(FireStoreTAbles.Note).document(note.id)
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been delete successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }
}