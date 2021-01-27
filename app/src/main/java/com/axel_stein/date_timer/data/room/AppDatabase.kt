package com.axel_stein.date_timer.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.axel_stein.date_timer.data.room.dao.TimerDao
import com.axel_stein.date_timer.data.room.model.Timer

@Database(
    entities = [
        Timer::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao
}