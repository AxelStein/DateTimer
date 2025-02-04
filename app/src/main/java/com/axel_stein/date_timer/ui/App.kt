package com.axel_stein.date_timer.ui

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.axel_stein.date_timer.BuildConfig
import com.axel_stein.date_timer.data.dagger.AppComponent
import com.axel_stein.date_timer.data.dagger.AppModule
import com.axel_stein.date_timer.data.dagger.DaggerAppComponent
import com.axel_stein.date_timer.ui.reminder.ReminderWorker
import com.google.android.gms.ads.MobileAds
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers.io
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class App : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    @Inject
    lateinit var billingManager: BillingManager

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

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

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                billingManager.onStart()
            }

            override fun onStop(owner: LifecycleOwner) {
                billingManager.onStop()
            }
        })

        MobileAds.initialize(this)
    }
}