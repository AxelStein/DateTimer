package com.axel_stein.date_timer.data.dagger

import com.axel_stein.date_timer.ui.App
import com.axel_stein.date_timer.ui.MainActivity
import com.axel_stein.date_timer.ui.edit_timer.EditTimerViewModel
import com.axel_stein.date_timer.ui.preferences.PreferencesFragment
import com.axel_stein.date_timer.ui.reminder.AndroidNotificationTray
import com.axel_stein.date_timer.ui.timers.TimersFragment
import com.axel_stein.date_timer.ui.timers.TimersViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(vm: TimersViewModel)
    fun inject(vm: EditTimerViewModel)
    fun inject(fragment: TimersFragment)
    fun inject(notificationTray: AndroidNotificationTray)
    fun inject(fragment: PreferencesFragment)
    fun inject(fragment: MainActivity)
    fun inject(app: App)
}