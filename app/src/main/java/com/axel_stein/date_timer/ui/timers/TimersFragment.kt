package com.axel_stein.date_timer.ui.timers

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.databinding.FragmentTimersBinding
import com.axel_stein.date_timer.ui.App
import com.axel_stein.date_timer.utils.LinearLayoutManagerWrapper
import com.axel_stein.date_timer.utils.setVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import javax.inject.Inject

class TimersFragment : Fragment() {
    private lateinit var binding: FragmentTimersBinding
    private val viewModel: TimersViewModel by viewModels()
    private val listAdapter = TimersAdapter()
    private lateinit var settings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        App.appComponent.inject(this)
    }

    @Inject
    fun inject(settings: AppSettings) {
        this.settings = settings
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
        listAdapter.setOnTimerCompletedListener { timer ->
            viewModel.completeTimer(timer)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemsLiveData.observe(viewLifecycleOwner, {
            listAdapter.submitList(it)
            binding.noTimers.setVisible(it.isNullOrEmpty())
        })
        viewModel.showMessageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(view, msg, LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_timers, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.menu_show_completed_timers).isChecked = settings.showCompletedTimers()
        menu.findItem(R.id.menu_show_paused_timers).isChecked = settings.showPausedTimers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_timer -> findNavController().navigate(R.id.action_add_timer)
            R.id.menu_show_completed_timers -> {
                item.isChecked = !item.isChecked
                viewModel.showCompleted(item.isChecked)
            }
            R.id.menu_show_paused_timers -> {
                item.isChecked = !item.isChecked
                viewModel.showPaused(item.isChecked)
            }
            R.id.menu_sort_timers_title -> viewModel.sortByTitle()
            R.id.menu_sort_timers_date -> viewModel.sortByDate()
        }
        return super.onOptionsItemSelected(item)
    }
}