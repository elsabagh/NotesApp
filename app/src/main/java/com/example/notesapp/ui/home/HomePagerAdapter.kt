package com.example.notesapp.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.notesapp.ui.note.NoteListingFragment
import com.example.notesapp.ui.task.TaskListingFragment
import com.example.notesapp.util.HomeTabs

class HomePagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount():
            Int = HomeTabs.values().size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            HomeTabs.Notes.index ->
                NoteListingFragment.newInstance(HomeTabs.Notes.name)

            HomeTabs.Tasks.index ->
                TaskListingFragment.newInstance(HomeTabs.Tasks.name)

            else -> throw IllegalStateException("")
        }
    }
}