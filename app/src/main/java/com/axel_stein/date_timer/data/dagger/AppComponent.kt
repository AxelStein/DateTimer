package com.axel_stein.date_timer.data.dagger

import com.axel_stein.date_timer.ui.timers.TimersViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(vm: TimersViewModel)
}