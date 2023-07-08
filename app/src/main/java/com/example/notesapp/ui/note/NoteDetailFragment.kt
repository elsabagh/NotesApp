package com.example.notesapp.ui.note

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R
import com.example.notesapp.data.model.Note
import com.example.notesapp.databinding.FragmentNoteDetailBinding
import com.example.notesapp.ui.auth.AuthViewModel
import com.example.notesapp.util.*
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class NoteDetailFragment : Fragment() {

    private lateinit var binding: FragmentNoteDetailBinding
    private val viewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    var objNote: Note? = null
    var tagsList: MutableList<String> = arrayListOf()
    var imageUris: MutableList<Uri> = arrayListOf()
    private val adapter by lazy {
        ImageListingAdapter(
            onCancelClicked = { pos, item ->
                onRemoveImage(pos, item)
            }
        )
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    imageUris.add(fileUri)
                    adapter.updateList(imageUris)
                    binding.btnProgressAr.hide()
                    toggleDoneButtonVisibility()

                }

                ImagePicker.RESULT_ERROR -> {
                    binding.btnProgressAr.hide()
                    toast(ImagePicker.getError(data))
                }

                else -> {
                    binding.btnProgressAr.hide()
                    Log.e(TAG, "Task Cancelled")
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (this::binding.isInitialized) {
            binding.root
        } else {
            binding = FragmentNoteDetailBinding.inflate(layoutInflater)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()
        observer()
    }


    private fun observer() {
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }

                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.apply {
                        btnProgressAr.hide()
                        done.hide()
                        delete.show()
                        edit.show()
                        isMakeEnableUI(false)
                        toast(state.data.second)
                        objNote = state.data.first
                    }
                }
            }
        }
        viewModel.updateNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }

                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.apply {
                        btnProgressAr.hide()
                        toast(state.data)
                        done.hide()
                        edit.show()
                        isMakeEnableUI(false)
                    }

                }
            }
        }

        viewModel.deleteNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }

                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    toast(state.data)
                    findNavController().navigateUp()
                }
            }
        }

        viewModel.deleteImage.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnProgressAr.show()
                }
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.apply {
                        btnProgressAr.hide()
                        toast(state.data)
                        done.show()
                        edit.hide()
                        isMakeEnableUI(false)
                    }
                }
            }
        }
    }


    private fun updateUi() {
        val sdf = SimpleDateFormat("dd MMM yyyy . hh:mm a")
        objNote = arguments?.getParcelable("note")
        binding.tags.layoutParams.height = 40.dpToPx
        objNote?.let { note ->
            binding.apply {
                title.setText(note.title)
                date.setText(sdf.format(note.date))
                description.setText(note.description)
                done.hide()
                edit.show()
                delete.show()
            }
            tagsList = note.tags
            addTags(tagsList)
            isMakeEnableUI(false)

        } ?: run {
            binding.apply {
                title.setText("")
                date.setText(sdf.format(Date()))
                description.setText("")
                done.hide()
                edit.hide()
                delete.hide()
            }
            isMakeEnableUI(true)
        }
        binding.images.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.images.adapter = adapter
        binding.images.itemAnimator = null
        imageUris = objNote?.images?.map { it.toUri() }?.toMutableList() ?: arrayListOf()
        adapter.updateList(imageUris)
        binding.addImageLl.setOnClickListener {
            binding.btnProgressAr.show()
            ImagePicker.with(this)
                //.crop()
                .compress(1024)
                .galleryOnly()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        binding.apply {
            back.setOnClickListener {
                findNavController().navigateUp()
            }
            title.setOnClickListener {
                isMakeEnableUI(true)
            }
            description.setOnClickListener {
                isMakeEnableUI(true)
            }
            delete.setOnClickListener {
                objNote?.let { viewModel.deleteNote(it) }
            }
            addTagLl.setOnClickListener {
                showAddTagDialog()
            }
            edit.setOnClickListener {
                isMakeEnableUI(true)
                done.show()
                edit.hide()
                title.requestFocus()
            }
            done.setOnClickListener {
                if (validation()) {
                    onDonePressed()
                }
            }
            title.doAfterTextChanged {
                done.show()
                edit.hide()
            }
            description.doAfterTextChanged {
                done.show()
                edit.hide()
            }
        }

    }

    private fun showAddTagDialog() {
        val dialog = requireContext().createDialog(R.layout.add_tag_dialog, true)
        val button = dialog.findViewById<MaterialButton>(R.id.tag_dialog_add)
        val editText = dialog.findViewById<EditText>(R.id.tag_dialog_et)
        button.setOnClickListener {
            if (editText.text.toString().isNullOrEmpty()) {
                toast(getString(R.string.error_tag_text))
            } else {
                val text = editText.text.toString()
                tagsList.add(text)
                binding.tags.apply {
                    addChip(text, true) {
                        tagsList.forEachIndexed { index, tag ->
                            if (text.equals(tag)) {
                                tagsList.removeAt(index)
                                binding.tags.removeViewAt(index)
                            }
                        }
                        if (tagsList.size == 0) {
                            layoutParams.height = 40.dpToPx
                        }
                    }
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                binding.done.show()
                binding.edit.hide()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun addTags(note: MutableList<String>) {
        if (note.size > 0) {
            binding.tags.apply {
                removeAllViews()
                note.forEachIndexed { index, tag ->
                    addChip(tag, true) {
                        if (isEnabled) {
                            note.removeAt(index)
                            this.removeViewAt(index)
                        }
                    }
                }
            }
        }
    }

    private fun getNote(): Note {
        return Note(
            id = objNote?.id ?: "",
            title = binding.title.text.toString(),
            description = binding.description.text.toString(),
            tags = tagsList,
            date = Date(),
            images = getImageUrls()
        ).apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }

    private fun getImageUrls(): List<String> {
        return if (imageUris.isNotEmpty()) {
            imageUris.map {
                it.toString()
            }
        } else {
            objNote?.images ?: arrayListOf()
        }
    }

    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.apply {
            title.isEnabled = isDisable
            date.isEnabled = isDisable
            tags.isEnabled = isDisable
            addTagLl.isEnabled = isDisable
            description.isEnabled = isDisable
        }

    }

    private fun toggleDoneButtonVisibility() {
        if (objNote != null) {
            binding.edit.performClick()
        }
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
        adapter.removeItem(pos)
        imageUris.remove(item)
        objNote?.let { note ->
            val updatedImages = note.images.toMutableList()
            updatedImages.remove(item.toString())
            note.images = updatedImages
        }
        toggleDoneButtonVisibility()

        val updatedNoteImages = imageUris.map { it.toString() }
        objNote?.images = updatedNoteImages

        // Update the image URLs for the remaining images
        adapter.updateList(imageUris)
    }

    private fun onDonePressed() {
        if (imageUris.isNotEmpty()) {
            viewModel.onUploadFile(imageUris) { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.btnProgressAr.show()
                    }

                    is UiState.Failure -> {
                        binding.btnProgressAr.hide()
                        toast(state.error)
                    }

                    is UiState.Success -> {
                        binding.btnProgressAr.hide()
                        if (objNote == null) {
                            viewModel.addNote(getNote())
                        } else {
                            viewModel.updateNote(getNote())
                        }
                        // Delete the images from Firebase Storage
                        state.data.forEach { imageUrl ->
                            // Delete the image using the imageUrl
                            // Example code to delete the image:
                            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                                imageUrl.toString()
                            )
                            storageRef.delete()
                                .addOnSuccessListener {
                                    // Image deleted successfully
                                }
                                .addOnFailureListener { e ->
                                    // Error deleting image
                                }
                        }
                    }
                }
            }
        } else {
            if (objNote == null) {
                viewModel.addNote(getNote())
            } else {
                viewModel.updateNote(getNote())
            }
        }
    }


    private fun validation(): Boolean {
        var isValid = true
        if (binding.title.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_title))
        }
        if (binding.description.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_description))
        }
        return isValid
    }

    companion object {
        const val TAG: String = "NoteDetailFragment"
    }
}