package com.axel_stein.date_timer.data

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.axel_stein.date_timer.data.AppSettings.TimersSort.DATE
import com.axel_stein.date_timer.data.AppSettings.TimersSort.TITLE

class AppSettings(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        applyTheme(prefs.getString("theme", "system") ?: "system")
    }

    fun applyTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(when (theme) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        })
    }

    fun setShowCompletedTimers(show: Boolean) {
        prefs.edit().putBoolean("show_completed_timers", show).apply()
    }

    fun showCompletedTimers() = prefs.getBoolean("show_completed_timers", false)

    fun setShowPausedTimers(show: Boolean) {
        prefs.edit().putBoolean("show_paused_timers", show).apply()
    }

    fun showPausedTimers() = prefs.getBoolean("show_paused_timers", false)

    fun sortTimersByTitle() {
        prefs.edit().putString("sort_timers", TITLE.name).apply()
    }

    fun sortTimersByDate() {
        prefs.edit().putString("sort_timers", DATE.name).apply()
    }

    fun getTimersSort() = TimersSort.valueOf(prefs.getString("sort_timers", DATE.name) ?: DATE.name)

    fun notificationLed() = prefs.getBoolean("notification_led", true)

    fun notificationVibration() = prefs.getBoolean("notification_vibration", true)

    fun remindersEnabled() = prefs.getBoolean("reminders_enabled", true)

    fun getRingtoneUri(): String {
        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        return prefs.getString("ringtone_uri", defaultRingtoneUri) ?: defaultRingtoneUri
    }

    fun setRingtoneUri(uri: Uri?) {
        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        prefs.edit().putString("ringtone_uri", uri?.toString() ?: defaultRingtoneUri).apply()
    }

    fun setAdsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("ads_enabled", enabled).commit()
    }

    fun isAdsEnabled() = prefs.getBoolean("ads_enabled", true)

    fun setAdProposalVisible(enabled: Boolean) {
        prefs.edit().putBoolean("ad_proposal_visible", enabled).commit()
    }

    fun isAdProposalVisible() = prefs.getBoolean("ad_proposal_visible", true)

    enum class TimersSort {
        TITLE,
        DATE
    }
}