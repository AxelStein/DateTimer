package com.axel_stein.date_timer.ui.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.ui.App
import com.axel_stein.date_timer.ui.MainActivity
import javax.inject.Inject

class AndroidNotificationTray(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private lateinit var settings: AppSettings
    private lateinit var ringtoneHelper: RingtoneHelper

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun inject(settings: AppSettings, ringtoneHelper: RingtoneHelper) {
        this.settings = settings
        this.ringtoneHelper = ringtoneHelper
    }

    fun showNotification(title: String) {
        if (!settings.remindersEnabled()) return
        val id = title.hashCode()
        try {
            createNotificationChannel()
            notify(id, buildNotification(title))
        } catch (e: Exception) {
            // Some Xiaomi phones produce a RuntimeException if custom notification sounds are used.
            Log.e(
                "AndroidNotificationTray",
                "Failed to show notification. Retrying with default sound."
            )
            notify(id, buildNotification(title, defaultSound = true))

        }
    }

    private fun notify(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
        if (SDK_INT >= N) {
            notificationManager.notify(0, buildSummaryNotification())
        }
    }

    private fun buildNotification(title: String, defaultSound: Boolean = false): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_count_down)
            .setContentTitle(title)
            .setStyle(NotificationCompat.InboxStyle())
            .setGroup(GROUP_ID)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(launchAppIntent())
        builder.setSound(
            if (defaultSound) {
                ringtoneHelper.defaultUri()
            } else {
                ringtoneHelper.getUri()
            }
        )
        if (settings.notificationLed()) {
            builder.setLights(Color.RED, 1000, 1000)
        }
        if (settings.notificationVibration()) {
            builder.setVibrate(longArrayOf(500, 500, 500, 500, 500, 500, 500, 500))
        }
        return builder.build()
    }

    private fun launchAppIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)
    }

    private fun buildSummaryNotification(): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_count_down)
            .setGroup(GROUP_ID)
            .setGroupSummary(true)
        return builder.build()
    }

    private fun createNotificationChannel() {
        if (SDK_INT >= O) {
            val mChannel = NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_name), IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private const val APP_ID = "com.axel_stein.date_timer:"
        private const val CHANNEL_ID = APP_ID + "CHANNEL_ID"
        private const val GROUP_ID = APP_ID + "GROUP_ID"
    }
}