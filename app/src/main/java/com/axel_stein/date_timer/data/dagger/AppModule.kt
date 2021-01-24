package com.axel_stein.date_timer.data.dagger

import android.content.Context
import androidx.room.Room
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.data.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, context.packageName).build()
    }

    @Provides
    fun provideTimerDao(db: AppDatabase) = db.timerDao()

    @Provides
    @Singleton
    fun provideSettings() = AppSettings(context)
}