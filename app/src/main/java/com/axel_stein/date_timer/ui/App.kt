package com.axel_stein.date_timer.ui

import android.app.Application
import com.axel_stein.date_timer.data.dagger.AppComponent
import com.axel_stein.date_timer.data.dagger.AppModule
import com.axel_stein.date_timer.data.dagger.DaggerAppComponent

class App : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}