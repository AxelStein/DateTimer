package com.axel_stein.date_timer.ui.timers

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.utils.formatDateTime
import org.joda.time.DateTime
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
    private var dateTime: DateTime? = null
    private var visible = true
    private var running = true
    private val tickRunnable = object : Runnable {
        override fun run() {
            if (running) {
                updateText()
                postDelayed(this, 1000)
            }
        }
    }
    private var countDown = true
    var onCountDownEnd: (() -> Unit)? = null

    fun setDateTime(dateTime: DateTime?) {
        this.dateTime = dateTime
        updateRunning()
    }

    fun setCountDown(value: Boolean) {
        countDown = value
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
        val r = visible && isShown && dateTime != null
        if (r != running) {
            running = r
            if (running) {
                tickRunnable.run()
            } else {
                removeCallbacks(tickRunnable)
            }
        }
    }

    // binding.timer.text = formatDateTime(itemView.context, item.dateTime)
    private fun updateText() {
        dateTime?.let {
            val ms = System.currentTimeMillis()
            if (countDown) {
                period.setPeriod(ms, it.millis)
                if (ms >= it.millis) {
                    running = false
                    onCountDownEnd?.invoke()
                    text = formatDateTime(context, it)
                    return
                }
            } else {
                period.setPeriod(it.millis, ms)
            }
            text = periodFormatter.print(period)
        }
    }
}