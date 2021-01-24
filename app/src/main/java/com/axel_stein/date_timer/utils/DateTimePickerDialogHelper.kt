package com.axel_stein.date_timer.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import org.joda.time.DateTime

fun showDatePicker(
    context: Context,
    dateTime: DateTime,
    callback: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> callback(year, month + 1, dayOfMonth) },
        dateTime.year, dateTime.monthOfYear-1, dateTime.dayOfMonth
    ).show()
}

fun showTimePicker(
    context: Context,
    dateTime: DateTime,
    callback: (hourOfDay: Int, minuteOfHour: Int) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minuteOfHour -> callback(hourOfDay, minuteOfHour) },
        dateTime.hourOfDay, dateTime.minuteOfHour, is24HourFormat(context)
    ).show()
}