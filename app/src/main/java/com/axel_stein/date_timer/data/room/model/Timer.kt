package com.axel_stein.date_timer.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "timers")
data class Timer(
    var title: String,

    @ColumnInfo(name = "date_time")
    var dateTime: DateTime,

    var paused: Boolean = false,

    var completed: Boolean = false
) {
    @PrimaryKey
    var id = 0L
}