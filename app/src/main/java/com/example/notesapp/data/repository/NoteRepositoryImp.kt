package com.example.notesapp.data.repository

import android.net.Uri
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.User
import com.example.notesapp.util.FireStoreDocumentField
import com.example.notesapp.util.FireStoreTAbles
import com.example.notesapp.util.FirebaseStorageConstants.NOTE_IMAGES
import com.example.notesapp.util.UiState
import com.example.notesapp.util.endOfDay
import com.example.notesapp.util.startOfDay
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class NoteRepositoryImp(
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : NoteRepository {

    override fun getNotes(user: User?, date: Date?, result: (UiState<List<Note>>) -> Unit) {
        var query = database.collection(FireStoreTAbles.Note)
            .whereEqualTo(FireStoreDocumentField.USER_ID, user?.id)

        if (date != null) {
            val startOfDay = date.startOfDay()
            val endOfDay = date.endOfDay()
            query = query.whereGreaterThanOrEqualTo(FireStoreDocumentField.DATE, startOfDay)
                .whereLessThanOrEqualTo(FireStoreDocumentField.DATE, endOfDay)
        }

        query.orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
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

    override suspend fun uploadFile(
        fileUri: List<Uri>,
        onResult: (UiState<List<Uri>>) -> Unit
    ) {
        try {
            val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storageReference.child(NOTE_IMAGES)
                            .child(image.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }

    override fun deleteImage(note: Note, imageUrl: String, result: (UiState<String>) -> Unit) {
        // Delete the image from the storage using the imageUrl

        // Update the note's images list by removing the deleted image
        val updatedImages = note.images.toMutableList()
        updatedImages.remove(imageUrl)
        note.images = updatedImages

        // Update the note in the Firestore database
        val document = database.collection(FireStoreTAbles.Note).document(note.id)
        document.set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Image has been deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }


}