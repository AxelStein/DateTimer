package com.axel_stein.date_timer.ui.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.axel_stein.date_timer.data.room.model.Timer

class ReminderScheduler(private val context: Context) {
    private val am = context.getSystemService(ALARM_SERVICE) as AlarmManager

    fun schedule(timer: Timer) {
        cancel(timer)
        if (!timer.paused && !timer.completed && timer.countDown) {
            val pi = pendingIntent(timer)
            AlarmManagerCompat.setAlarmClock(am, timer.dateTime.millis, pi, pi)
        }
    }

    fun cancel(timer: Timer) {
        am.cancel(pendingIntent(timer))
    }

    fun cancelById(id: Long) {
        am.cancel(pendingIntent(id))
    }

    private fun pendingIntent(timer: Timer) = pendingIntent(timer.id, timer.title)

    private fun pendingIntent(id: Long, title: String = ""): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.axel_stein.date_timer.SHOW_REMINDER"
            putExtra("title", title)
        }
        return PendingIntent.getBroadcast(context, id.toInt(), intent, FLAG_UPDATE_CURRENT)
    }
}