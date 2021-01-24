package com.axel_stein.date_timer.data.dagger

import com.axel_stein.date_timer.ui.edit_timer.EditTimerViewModel
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
}