package com.axel_stein.date_timer.ui.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent == null) return

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.axel_stein.date_timer:reminder")
        wl.acquire(60*1000L)

        val notificationTray = AndroidNotificationTray(context)
        notificationTray.showNotification(intent.getStringExtra("title") ?: "")
    }
}