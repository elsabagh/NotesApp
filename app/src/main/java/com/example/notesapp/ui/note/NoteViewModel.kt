package com.example.notesapp.ui.note

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.User
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableLiveData<UiState<List<Note>>>()
    val note: LiveData<UiState<List<Note>>>
        get() = _notes

    private val _addNote = MutableLiveData<UiState<Pair<Note, String>>>()
    val addNote: LiveData<UiState<Pair<Note, String>>>
        get() = _addNote

    private val _updateNote = MutableLiveData<UiState<String>>()
    val updateNote: LiveData<UiState<String>>
        get() = _updateNote

    private val _deleteNote = MutableLiveData<UiState<String>>()
    val deleteNote: LiveData<UiState<String>>
        get() = _deleteNote

    private val _deleteImage = MutableLiveData<UiState<String>>()
    val deleteImage: LiveData<UiState<String>>
        get() = _deleteImage


    fun getNotes(user: User?, date: Date? = null) {
        _notes.value = UiState.Loading
        repository.getNotes(user, date) { _notes.value = it }
    }

    fun addNote(note: Note) {
        _addNote.value = UiState.Loading
        repository.addNote(note) { _addNote.value = it }
    }

    fun updateNote(note: Note) {
        _updateNote.value = UiState.Loading
        repository.updateNote(note) { _updateNote.value = it }
    }

    fun deleteNote(note: Note) {
        _deleteNote.value = UiState.Loading
        repository.deleteNote(note) { _deleteNote.value = it }
    }

    fun onUploadFile(fileUris: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            repository.uploadFile(fileUris, onResult)
        }
    }

    fun deleteImage(note: Note, imageUrl: String) {
        _deleteImage.value = UiState.Loading
        repository.deleteImage(note, imageUrl) { result ->
            _deleteImage.value = result
        }
    }
}