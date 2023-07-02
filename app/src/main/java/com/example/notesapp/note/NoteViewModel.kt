package com.example.notesapp.note

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableLiveData<UiState<List<Note>>>()
    val note: LiveData<UiState<List<Note>>>
        get() = _notes

    private val _addNotes = MutableLiveData<UiState<String>>()
    val addNote: LiveData<UiState<String>>
        get() = _addNotes

    fun getNotes() {
        _notes.value = UiState.Loading
        repository.getNotes {
            _notes.value = it
        }
    }

    fun addNotes(note: Note) {
        _addNotes.value = UiState.Loading
        repository.addNote(note) {
            _addNotes.value = it
        }
    }

}