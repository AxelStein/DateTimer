package com.axel_stein.date_timer.ui.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.axel_stein.date_timer.R

class AndroidNotificationTray(private val context: Context) {
    fun showNotification(title: String) {
        val notificationManager = NotificationManagerCompat.from(context)
        val notification: Notification = buildNotification(title)
        createNotificationChannel()
        notificationManager.notify(title.hashCode(), notification)
        if (SDK_INT >= N) {
            notificationManager.notify(0, buildSummaryNotification())
        }
    }

    private fun buildNotification(title: String): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_count_down)
            .setContentTitle(title)
            .setStyle(NotificationCompat.InboxStyle())
            .setGroup(GROUP_ID)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setVibrate(longArrayOf(500, 500, 500, 500, 500, 500, 500, 500))
            .setPriority(NotificationCompat.PRIORITY_MAX)
        return builder.build()
    }

    private fun buildSummaryNotification(): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_count_down)
            .setGroup(GROUP_ID)
            .setGroupSummary(true)
        return builder.build()
    }

    private fun createNotificationChannel() {
        val notificationManager = NotificationManagerCompat.from(context)
        if (SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            mChannel.description = CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private const val APP_ID = "com.axel_stein.date_timer:"
        private const val CHANNEL_ID = APP_ID + "CHANNEL_ID"
        private const val CHANNEL_NAME = "Reminders"
        private const val CHANNEL_DESCRIPTION = "Date timer reminders"
        private const val GROUP_ID = APP_ID + "GROUP_ID"
    }
}