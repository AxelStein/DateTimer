package com.axel_stein.date_timer.ui.timers

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.room.model.Timer
import com.axel_stein.date_timer.utils.formatDateTime
import org.joda.time.MutablePeriod
import org.joda.time.format.PeriodFormatterBuilder

class TimerView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val periodFormatter = PeriodFormatterBuilder()
        .appendYears()
        .appendSuffix(context.getString(R.string.suffix_year))
        .appendMonths()
        .appendSuffix(context.getString(R.string.suffix_month))
        .appendWeeks()
        .appendSuffix(context.getString(R.string.suffix_week))
        .appendDays()
        .appendSuffix(context.getString(R.string.suffix_day))
        .minimumPrintedDigits(2)
        .printZeroAlways()
        .appendHours()
        .appendSeparator(":")
        .appendMinutes()
        .appendSeparator(":")
        .appendSeconds()
        .toFormatter()

    companion object {
        private const val STATE_NONE = -1
        private const val STATE_PAUSED = 0
        private const val STATE_COUNTDOWN_COMPLETED = 1
        private const val STATE_COUNTDOWN_RUNNING = 2
        private const val STATE_RUNNING = 3
    }

    private val period = MutablePeriod()
    private var timer: Timer? = null
    private var visible = true
    private var running = false
    private var state = STATE_NONE
    private val tickRunnable = object : Runnable {
        override fun run() {
            if (running) {
                updateState()
                updateText()
                postDelayed(this, 1000)
            }
        }
    }
    var onTimerCompleted: (() -> Unit)? = null

    fun setTimer(timer: Timer) {
        this.timer = timer
        updateState()
        updateText()
        updateRunning()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        visible = false
        updateRunning()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        visible = visibility == VISIBLE
        updateRunning()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        updateRunning()
    }

    private fun updateRunning() {
        val r = visible && isShown &&
            (state == STATE_COUNTDOWN_RUNNING || state == STATE_RUNNING)
        if (r != running) {
            running = r
            if (running) {
                tickRunnable.run()
            } else {
                removeCallbacks(tickRunnable)
            }
        }
    }

    private fun updateState() {
        running = false
        val it = timer
        state = if (it != null) {
            val ms = System.currentTimeMillis()
            when {
                it.paused -> STATE_PAUSED
                it.completed -> STATE_COUNTDOWN_COMPLETED
                it.countDown && ms >= it.dateTime.millis -> STATE_COUNTDOWN_COMPLETED
                !it.paused && it.countDown -> {
                    running = true
                    STATE_COUNTDOWN_RUNNING
                }
                !it.paused && !it.countDown -> {
                    running = true
                    STATE_RUNNING
                }
                else -> STATE_NONE
            }
        } else {
            STATE_NONE
        }
    }

    private fun updateText() {
        when (state) {
            STATE_NONE -> text = ""
            STATE_PAUSED -> text = formatDateTime(context, timer?.dateTime)
            STATE_RUNNING -> {
                val ms = System.currentTimeMillis()
                period.setPeriod(timer?.dateTime?.millis ?: 0, ms)
                text = periodFormatter.print(period)
            }
            STATE_COUNTDOWN_RUNNING -> {
                val ms = System.currentTimeMillis()
                period.setPeriod(ms, timer?.dateTime?.millis ?: 0)
                text = periodFormatter.print(period)
            }
            STATE_COUNTDOWN_COMPLETED -> {
                if (timer?.completed != true) {
                    onTimerCompleted?.invoke()
                }
                text = formatDateTime(context, timer?.dateTime)
            }
        }
    }
}