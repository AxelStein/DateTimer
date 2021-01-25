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


class TimersAdapter : ListAdapter<Timer, TimersAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Timer>() {
        override fun areItemsTheSame(a: Timer, b: Timer): Boolean {
            return a.id == b.id
        }

        override fun areContentsTheSame(a: Timer, b: Timer): Boolean {
            return a.id == b.id &&
                a.title == b.title &&
                a.paused == b.paused &&
                a.dateTime == b.dateTime
        }
    }

    var onTimerClickListener: ((timer: Timer) -> Unit)? = null
    var onPauseClickListener: ((timer: Timer) -> Unit)? = null
    var onTimerCompletedListener: ((timer: Timer) -> Unit)? = null

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
            binding.timer.setTimer(timer)
            binding.timer.onTimerCompleted = {
                binding.buttonPause.setVisible(false)
                binding.iconDone.setVisible(true)
                onTimerCompletedListener?.invoke(timer)
            }
            binding.buttonPause.setImageResource(
                if (timer.paused) R.drawable.icon_resume
                else R.drawable.icon_pause
            )
        }
    }
}

private fun ViewGroup.inflate(layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}
