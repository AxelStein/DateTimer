package com.axel_stein.date_timer.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun dateToStr(date: DateTime?): String? = date?.toString()

    @TypeConverter
    fun strToDate(s: String?): DateTime? = if (s == null) null else DateTime(s)
}