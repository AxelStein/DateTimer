package com.axel_stein.date_timer.ui.timers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.room.model.Timer
import com.axel_stein.date_timer.databinding.ItemTimerBinding
import com.axel_stein.date_timer.utils.setVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT


class TimersAdapter : ListAdapter<Timer, TimersAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Timer>() {
        override fun areItemsTheSame(a: Timer, b: Timer): Boolean {
            return a.id == b.id
        }

        override fun areContentsTheSame(a: Timer, b: Timer): Boolean {
            return a.id == b.id &&
                a.title == b.title &&
                a.paused == b.paused &&
                a.completed == b.completed &&
                a.countDown == b.countDown &&
                a.dateTime == b.dateTime
        }
    }

    var onTimerClickListener: ((timer: Timer) -> Unit)? = null
    var onPauseClickListener: ((timer: Timer) -> Unit)? = null
    private var onTimerCompletedListener: ((timer: Timer) -> Unit)? = null

    private val completedTimers = mutableSetOf<Timer>()

    fun setOnTimerCompletedListener(listener: (timer: Timer) -> Unit) {
        onTimerCompletedListener = listener
        for (timer in completedTimers) {
            listener.invoke(timer)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(parent.inflate(R.layout.item_timer))
        vh.itemView.setOnClickListener {
            onTimerClickListener?.invoke(getItem(vh.adapterPosition))
        }
        vh.binding.buttonPause.setOnClickListener {
            onPauseClickListener?.invoke(getItem(vh.adapterPosition))
        }
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTimerBinding.bind(view)

        fun setItem(timer: Timer) {
            binding.icon.setImageResource(
                if (timer.countDown) R.drawable.icon_count_down
                else R.drawable.icon_count_up
            )
            binding.title.text = timer.title
            binding.timer.onTimerCompleted = {
                if (itemView.isAttachedToWindow) {
                    Snackbar.make(itemView, R.string.msg_timer_completed, LENGTH_SHORT).show()
                }
                onTimerCompletedListener?.invoke(timer) ?: completedTimers.add(timer)
            }
            binding.timer.setTimer(timer)
            binding.buttonPause.setImageResource(
                if (timer.paused) R.drawable.icon_resume
                else R.drawable.icon_pause
            )
            binding.buttonPause.setVisible(!timer.completed)
            binding.iconDone.setVisible(timer.completed)
        }
    }
}

private fun ViewGroup.inflate(layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}
