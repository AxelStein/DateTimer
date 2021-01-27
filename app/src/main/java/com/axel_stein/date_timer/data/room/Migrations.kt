@file:Suppress("FunctionName")

package com.axel_stein.date_timer.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

fun migrate_1_2() = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(""" 
            ALTER TABLE timers ADD COLUMN paused_date_time TEXT
        """)
    }
}