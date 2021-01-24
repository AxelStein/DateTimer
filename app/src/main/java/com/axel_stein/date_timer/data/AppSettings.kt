package com.axel_stein.date_timer.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.axel_stein.date_timer.data.AppSettings.TimersSort.DATE
import com.axel_stein.date_timer.data.AppSettings.TimersSort.TITLE

class AppSettings(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun setShowCompletedTimers(show: Boolean) {
        prefs.edit().putBoolean("show_completed_timers", show).apply()
    }

    fun showCompletedTimers() = prefs.getBoolean("show_completed_timers", true)

    fun sortTimersByTitle() {
        prefs.edit().putString("sort_timers", TITLE.name).apply()
    }

    fun sortTimersByDate() {
        prefs.edit().putString("sort_timers", DATE.name).apply()
    }

    fun getTimersSort() = TimersSort.valueOf(prefs.getString("sort_timers", TITLE.name) ?: TITLE.name)

    enum class TimersSort {
        TITLE,
        DATE
    }
}