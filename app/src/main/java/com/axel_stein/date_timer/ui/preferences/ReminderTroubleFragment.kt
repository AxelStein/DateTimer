package com.axel_stein.date_timer.ui.preferences

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.axel_stein.date_timer.databinding.FragmentReminderTroubleBinding

class ReminderTroubleFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentReminderTroubleBinding.inflate(inflater).root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}