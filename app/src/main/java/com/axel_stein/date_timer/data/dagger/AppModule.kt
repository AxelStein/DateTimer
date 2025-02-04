package com.axel_stein.date_timer.data.dagger

import android.content.Context
import androidx.room.Room
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.data.room.AppDatabase
import com.axel_stein.date_timer.data.room.migrate_1_2
import com.axel_stein.date_timer.ui.BillingManager
import com.axel_stein.date_timer.ui.reminder.ReminderScheduler
import com.axel_stein.date_timer.ui.reminder.RingtoneHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, context.packageName)
            .addMigrations(migrate_1_2())
            .build()
    }

    @Provides
    fun provideTimerDao(db: AppDatabase) = db.timerDao()

    @Provides
    @Singleton
    fun provideSettings() = AppSettings(context)

    @Provides
    @Singleton
    fun provideReminderScheduler() = ReminderScheduler(context)

    @Provides
    fun provideRingtoneHelper(settings: AppSettings) = RingtoneHelper(context, settings)

    @Provides
    @Singleton
    fun provideBillingManager(settings: AppSettings) = BillingManager(context, settings)
}