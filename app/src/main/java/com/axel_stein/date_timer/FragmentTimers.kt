package com.axel_stein.date_timer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.axel_stein.date_timer.databinding.FragmentTimersBinding

class FragmentTimers : Fragment() {
    private lateinit var binding: FragmentTimersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimersBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_timers, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_timer -> findNavController().navigate(R.id.action_add_timer)
            R.id.menu_show_completed_timers -> item.isChecked = !item.isChecked
        }
        return super.onOptionsItemSelected(item)
    }
}