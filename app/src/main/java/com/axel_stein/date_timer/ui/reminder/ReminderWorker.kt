package com.axel_stein.date_timer.ui.reminder

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.axel_stein.date_timer.data.dagger.AppModule

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            val appModule = AppModule(applicationContext)
            val dao = appModule.provideTimerDao(appModule.provideDatabase())
            val scheduler = appModule.provideReminderScheduler()

            val timers = dao.getActiveCountDownTimers()
            timers.forEach { scheduler.schedule(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
        return Result.success()
    }
}