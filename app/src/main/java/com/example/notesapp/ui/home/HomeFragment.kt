package com.example.notesapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.ui.auth.AuthViewModel
import com.example.notesapp.ui.note.NoteListingFragment
import com.example.notesapp.ui.task.TaskListingFragment
import com.example.notesapp.util.HomeTabs
import com.example.notesapp.util.onSelection
import com.example.notesapp.util.onTabSelectionListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_notes -> {
                    replaceFragment(NoteListingFragment.newInstance("Notes"))
                    true
                }
                R.id.action_tasks -> {
                    replaceFragment(TaskListingFragment.newInstance("Tasks"))
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            // Display the initial fragment (e.g., NoteListingFragment)
            replaceFragment(NoteListingFragment.newInstance("Notes"))
            // Set the corresponding menu item as checked in the BottomNavigationView
            binding.bottomNavigationView.selectedItemId = R.id.action_notes
        }

        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.logout.setOnClickListener {
//            authViewModel.logout {
//                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
//            }
//        }
//        binding.viewPager2.adapter = HomePagerAdapter(this)
//        viewPagerSetupWithTapLayout(
//            tabLayout = binding.tabLayout,
//            viewPager = binding.viewPager2
//        )
//        binding.tabLayout.onTabSelectionListener()
//    }
//
//    private fun viewPagerSetupWithTapLayout(tabLayout: TabLayout, viewPager: ViewPager2) {
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_tab, null)
//            val textViewTitle: TextView = view.findViewById<TextView>(R.id.tabTitle)
//            when (position) {
//                HomeTabs.Notes.index -> {
//                    tab.customView = view
//                    textViewTitle.text = getString(R.string.notes)
//                    tab.onSelection(true)
//                }
//
//                HomeTabs.Tasks.index -> {
//                    tab.customView = view
//                    textViewTitle.text = getString(R.string.tasks)
//                    tab.onSelection(false)
//                }
//            }
//        }.attach()
//    }

}