package com.axel_stein.date_timer.ui.reminder

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.RingtoneManager.*
import android.net.Uri
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.AppSettings

class RingtoneHelper(private val context: Context, private val settings: AppSettings) {
    fun getName(): String {
        val defaultName = context.resources.getString(R.string.ringtone_default)
        return try {
            var ringtoneName = defaultName
            val ringtone = getRingtone(context, getUri())
            if (ringtone != null) ringtoneName = ringtone.getTitle(context)
            ringtoneName
        } catch (e: RuntimeException) {
            e.printStackTrace()
            defaultName
        }
    }

    fun getUri(): Uri {
        val defaultRingtoneUri = RingtoneManager.getDefaultUri(TYPE_NOTIFICATION)
        val prefRingtoneUri = settings.getRingtoneUri()
        if (prefRingtoneUri.isNotEmpty())
            return Uri.parse(prefRingtoneUri)
        return defaultRingtoneUri
    }

    fun update(data: Intent?) {
        if (data == null) return
        val ringtoneUri = data.getParcelableExtra<Uri>(EXTRA_RINGTONE_PICKED_URI)
        settings.setRingtoneUri(ringtoneUri)
    }
}