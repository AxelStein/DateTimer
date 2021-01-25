package com.axel_stein.date_timer.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "timers")
data class Timer(
    var title: String = "",
    var paused: Boolean = false,
    var completed: Boolean = false,

    @ColumnInfo(name = "count_down")
    var countDown: Boolean = true,

    @ColumnInfo(name = "date_time")
    var dateTime: DateTime = DateTime(),
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}