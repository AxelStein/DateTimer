package com.axel_stein.date_timer.ui.edit_timer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.databinding.FragmentEditTimerBinding

class FragmentEditTimer : Fragment() {
    private lateinit var binding: FragmentEditTimerBinding
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        id = arguments?.getLong("id") ?: 0L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditTimerBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_edit_timer, menu)
        menu.findItem(R.id.menu_delete_timer)?.isVisible = id != 0L
        // todo pause
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}