package com.axel_stein.date_timer.data

import android.content.Context
import androidx.preference.PreferenceManager

class AppSettings(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun setShowCompletedTimers(show: Boolean) {
        prefs.edit().putBoolean("show_completed_timers", show).apply()
    }

    fun showCompletedTimers() = prefs.getBoolean("show_completed_timers", true)
}