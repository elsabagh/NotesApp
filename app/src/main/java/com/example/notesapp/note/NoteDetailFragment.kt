package com.example.notesapp.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.FragmentNoteDetailBinding
import com.example.notesapp.util.UiState
import com.example.notesapp.util.hide
import com.example.notesapp.util.show
import com.example.notesapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {

    val TAG: String = "NoteDetailFragment"
    lateinit var binding: FragmentNoteDetailBinding
    private val viewModel: NoteViewModel by viewModels()
    var isEdite = false
    var objNote: Note? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()
        binding.btnCreate.setOnClickListener {
            if (isEdite) {
                updateNote()
            } else {
                createNote()
            }
        }

    }

    private fun createNote() {
        if (validation()) {
            viewModel.addNotes(
                Note(
                    id = "",
                    text = binding.noteMsg.text.toString(),
                    date = Date()
                )
            )
        }
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                    binding.btnCreate.text = ""

                }

                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.btnCreate.text = "Create"
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.btnCreate.text = "Create"
                    toast(state.data)
                }
            }
        }
    }

    private fun updateNote() {
        if (validation()) {
            viewModel.updateNotes(
                Note(
                    id = objNote?.id ?: "",
                    text = binding.noteMsg.text.toString(),
                    date = Date()
                )
            )
        }
        viewModel.updateNotes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                    binding.btnCreate.text = ""

                }

                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    binding.btnCreate.text = "Update"
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    binding.btnCreate.text = "Update"
                    toast(state.data)
                }
            }

        }
    }

    private fun updateUi() {
        val type = arguments?.getString("type", null)
        type?.let {
            when (it) {
                "view" -> {
                    isEdite = false
                    binding.noteMsg.isEnabled = false
                    objNote = arguments?.getParcelable("note")
                    binding.noteMsg.setText(objNote?.text)
                    binding.btnCreate.hide()
                }

                "create" -> {
                    isEdite = false
                    binding.btnCreate.setText("Create")

                }

                "edit" -> {
                    isEdite = true
                    objNote = arguments?.getParcelable("note")
                    binding.noteMsg.setText(objNote?.text)
                    binding.btnCreate.setText("update")

                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.noteMsg.text.toString().isNullOrEmpty()) {
            isValid = false
            toast("Enter message")
        }
        return isValid
    }
}