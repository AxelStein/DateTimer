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

    private val period = MutablePeriod()
    private var timer: Timer? = null
    private var visible = true
    private var running = false
    private val tickRunnable = object : Runnable {
        override fun run() {
            if (running) {
                updateText()
                postDelayed(this, 1000)
            }
        }
    }
    var onTimerCompleted: (() -> Unit)? = null

    fun setTimer(timer: Timer) {
        this.timer = timer
        if (timer.paused || timer.completed) {
            text = formatDateTime(context, timer.dateTime)
        }
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
            timer != null &&
            timer?.paused != true &&
            timer?.completed != true

        if (r != running) {
            running = r
            if (running) {
                tickRunnable.run()
            } else {
                removeCallbacks(tickRunnable)
            }
        }
    }

    private fun updateText() {
        timer?.dateTime?.let {
            val ms = System.currentTimeMillis()
            if (timer?.countDown == true) {
                if (!checkTimerCompleted()) {
                    period.setPeriod(ms, it.millis)
                } else {
                    return
                }
            } else {
                period.setPeriod(it.millis, ms)
            }
            text = periodFormatter.print(period)
        }
    }

    private fun checkTimerCompleted(): Boolean {
        timer?.dateTime?.let {
            val ms = System.currentTimeMillis()
            if (timer?.countDown == true && ms >= it.millis) {
                running = false
                onTimerCompleted?.invoke()
                text = formatDateTime(context, it)
                return true
            }
        }
        return false
    }
}