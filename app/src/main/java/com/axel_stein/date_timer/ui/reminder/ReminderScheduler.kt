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

    private fun pendingIntent(timer: Timer): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.axel_stein.date_timer.SHOW_REMINDER"
            putExtra("title", timer.title)
        }
        return PendingIntent.getBroadcast(context, timer.id.toInt(), intent, FLAG_UPDATE_CURRENT)
    }
}