package com.axel_stein.date_timer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.axel_stein.date_timer.databinding.FragmentEditTimerBinding
import com.axel_stein.date_timer.databinding.FragmentTimersBinding

class FragmentEditTimer : Fragment() {
    private lateinit var binding: FragmentEditTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditTimerBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_edit_timer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}