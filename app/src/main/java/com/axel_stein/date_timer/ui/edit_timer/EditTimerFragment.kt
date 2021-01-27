package com.axel_stein.date_timer.ui.edit_timer

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.databinding.FragmentEditTimerBinding
import com.axel_stein.date_timer.ui.dialogs.ConfirmDialog
import com.axel_stein.date_timer.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

class EditTimerFragment : Fragment(), ConfirmDialog.OnConfirmListener {
    private val viewModel: EditTimerViewModel by viewModels { EditTimerFactory(this, id) }
    private lateinit var binding: FragmentEditTimerBinding
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        id = arguments?.getLong("id") ?: 0L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditTimerBinding.inflate(inflater)
        binding.editTitle.setupEditor {
            viewModel.setTitle(it)
        }
        binding.spinnerMode.onItemSelectedListener = setItemSelectedListener {
            viewModel.setCountDown(it == 0)
        }
        binding.spinnerMode.setVisible(id == 0L)
        binding.btnDate.setOnClickListener {
            showDatePicker(requireContext(), viewModel.getCurrentDateTime()) { year, month, dayOfMonth ->
                viewModel.setDate(year, month, dayOfMonth)
                binding.btnDate.text = formatDate(requireContext(), true, year, month, dayOfMonth)
            }
        }
        binding.btnTime.setOnClickListener {
            showTimePicker(requireContext(), viewModel.getCurrentDateTime()) { hourOfDay, minuteOfHour ->
                viewModel.setTime(hourOfDay, minuteOfHour)
                binding.btnTime.text = formatTime(requireContext(), hourOfDay, minuteOfHour)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timerLiveData.observe(viewLifecycleOwner, {
            binding.editTitle.setEditorText(it.title)
            binding.spinnerMode.setSelection(if (it.countDown) 0 else 1)
            binding.btnDate.text = formatDate(requireContext(), it.dateTime)
            binding.btnTime.text = formatTime(requireContext(), it.dateTime)
        })
        viewModel.errorTitleEmptyLiveData.observe(viewLifecycleOwner, {
            binding.inputLayoutTitle.showError(it, R.string.error_title_empty)
        })
        viewModel.showMessageLiveData.observe(viewLifecycleOwner, {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(view, msg, LENGTH_SHORT).show()
            }
        })
        viewModel.errorDateTimeLiveData.observe(viewLifecycleOwner, {
            binding.errorDateTime.setVisible(it != null)
            if (it != null) binding.errorDateTime.setText(it)
        })
        viewModel.actionFinishLiveData.observe(viewLifecycleOwner, {
            findNavController().navigateUp()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_edit_timer, menu)
        menu.findItem(R.id.menu_delete_timer)?.isVisible = id != 0L
        // todo pause
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save_timer -> {
                viewModel.save()
                true
            }
            R.id.menu_delete_timer -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_timer)
                    .positiveBtnText(R.string.action_delete)
                    .negativeBtnText(R.string.action_cancel)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfirm(tag: String?) {
        viewModel.delete()
    }
}