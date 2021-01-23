package com.axel_stein.date_timer.ui.timers

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.room.model.Timer
import com.axel_stein.date_timer.databinding.ItemTimerBinding
import org.joda.time.format.DateTimeFormat


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
                a.dateTime == b.dateTime
        }
    }

    var onTimerClickListener: ((timer: Timer) -> Unit)? = null
    var onPauseClickListener: ((timer: Timer) -> Unit)? = null

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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTimerBinding.bind(view)

        fun setItem(item: Timer) {
            binding.title.text = item.title
            if (item.paused || item.completed) {
                binding.countDown.setDateTime(null)
                binding.countDown.text = item.dateTime.toString(DateTimeFormat.mediumDateTime())
            } else {
                binding.countDown.setDateTime(item.dateTime)
            }
            binding.buttonPause.visibility = if (item.completed) GONE else VISIBLE
            binding.buttonPause.setImageResource(
                if (item.paused) R.drawable.icon_resume
                else R.drawable.icon_pause
            )
            binding.iconDone.visibility = if (item.completed) VISIBLE else GONE
        }
    }
}

private fun ViewGroup.inflate(layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}
