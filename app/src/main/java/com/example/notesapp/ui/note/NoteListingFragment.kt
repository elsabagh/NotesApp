package com.example.notesapp.ui.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentNoteListingBinding
import com.example.notesapp.ui.auth.AuthViewModel
import com.example.notesapp.util.UiState
import com.example.notesapp.util.hide
import com.example.notesapp.util.show
import com.example.notesapp.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class NoteListingFragment : Fragment() {

    private lateinit var binding: FragmentNoteListingBinding
    private val viewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    var param1: String? = null
    private var selectedDate: Date? = null
    private val dateFormat = SimpleDateFormat("MMMM d")

    private val adapter by lazy {
        NoteListingAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(
                    R.id.action_noteListingFragment_to_noteDetailFragment,
                    Bundle().apply {
                        putString("type", "view")
                        putParcelable("note", item)
                    })

            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (this::binding.isInitialized) {
            binding.root
        } else {
            binding = FragmentNoteListingBinding.inflate(layoutInflater)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager

        binding.btnCreate.setOnClickListener {
            findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment)
        }

        binding.calendarView.visibility = View.GONE
        binding.imgCalendar.setOnClickListener {
            if (binding.calendarView.visibility == View.VISIBLE) {
                binding.calendarView.visibility = View.GONE
            } else {
                // Set the calendar to the selectedDate (current date)
                val calendar = Calendar.getInstance()
                calendar.time = selectedDate ?: Date()
                binding.calendarView.date = calendar.timeInMillis
                binding.calendarView.visibility = View.VISIBLE
            }
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Create a Date object from the selected date
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.time

            // Update the selectedDate and the current date text
            this.selectedDate = selectedDate
            updateCurrentDateText(selectedDate)

            // Fetch notes for the selected date
            authViewModel.getSession {
                viewModel.getNotes(it, selectedDate)
            }
        }

        // If the selectedDate is null, it means it's the first time opening the app.
        // In that case, show notes for the current day by default.
        if (selectedDate == null) {
            val currentDate = Calendar.getInstance().time
            selectedDate = currentDate
            updateCurrentDateText(currentDate)
            authViewModel.getSession {
                viewModel.getNotes(it, currentDate)
            }
        }
        observer()

        binding.btnPrevDay.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate ?: Date()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            selectedDate = calendar.time
            updateCurrentDateText(selectedDate)
            binding.calendarView.date = selectedDate?.time ?: System.currentTimeMillis()
            authViewModel.getSession {
                viewModel.getNotes(it, selectedDate)
            }
        }

        binding.btnNextDay.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate ?: Date()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            selectedDate = calendar.time
            updateCurrentDateText(selectedDate)
            binding.calendarView.date = selectedDate?.time ?: System.currentTimeMillis()
            authViewModel.getSession {
                viewModel.getNotes(it, selectedDate)
            }
        }
    }

    private fun updateCurrentDateText(date: Date?) {
        binding.tvCurrentDate.text = dateFormat.format(date ?: Date())
    }


    private fun observer() {
        viewModel.note.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }

                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())
                }
            }
        }
    }
    companion object {
        const val TAG: String = "NoteListingFragment"
        @JvmStatic
        fun newInstance(param1: String) =
            NoteListingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}