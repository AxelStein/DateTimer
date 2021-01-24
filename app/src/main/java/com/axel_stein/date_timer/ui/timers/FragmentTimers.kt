package com.axel_stein.date_timer.ui.timers

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.databinding.FragmentTimersBinding
import com.axel_stein.date_timer.utils.LinearLayoutManagerWrapper
import com.axel_stein.date_timer.utils.setVisible

class FragmentTimers : Fragment() {
    private lateinit var binding: FragmentTimersBinding
    private val viewModel: TimersViewModel by viewModels()
    private val listAdapter = TimersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimersBinding.inflate(inflater)
        binding.recyclerView.layoutManager = LinearLayoutManagerWrapper(context)
        binding.recyclerView.adapter = listAdapter
        binding.recyclerView.setHasFixedSize(true)
        listAdapter.onTimerClickListener = { timer ->
            findNavController().navigate(R.id.action_edit_timer, Bundle().apply {
                putLong("id", timer.id)
            })
        }
        listAdapter.onPauseClickListener = { timer ->
            viewModel.pauseTimer(timer)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData.observe(viewLifecycleOwner, {
            listAdapter.submitList(it)
            binding.noTimers.setVisible(it.isNullOrEmpty())
        })
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