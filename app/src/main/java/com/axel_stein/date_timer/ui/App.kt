package com.axel_stein.date_timer.ui

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.axel_stein.date_timer.data.dagger.AppComponent
import com.axel_stein.date_timer.data.dagger.AppModule
import com.axel_stein.date_timer.data.dagger.DaggerAppComponent
import com.axel_stein.date_timer.ui.reminder.ReminderWorker
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io
import java.util.concurrent.TimeUnit

class App : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        Completable.fromAction {
            val tag = "com.axel_stein.date_timer:reminder_worker"
            val wm = WorkManager.getInstance(this)
            val infos = wm.getWorkInfosByTag(tag).get()
            if (infos.isEmpty()) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val request = PeriodicWorkRequestBuilder<ReminderWorker>(6, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .addTag(tag)
                    .build()
                wm.enqueue(request)
            }
        }.subscribeOn(io()).subscribe()
    }

}