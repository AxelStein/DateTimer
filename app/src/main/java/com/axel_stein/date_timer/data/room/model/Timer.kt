package com.axel_stein.date_timer.data.room.model

import android.os.Parcel
import android.os.Parcelable
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

    @ColumnInfo(name = "paused_date_time")
    var pausedDateTime: DateTime? = null,
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0L

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Timer> {
            override fun createFromParcel(source: Parcel?) = Timer(source)

            override fun newArray(size: Int) = arrayOfNulls<Timer>(size)
        }
    }

    private constructor(parcel: Parcel?) : this(
        title = parcel?.readString() ?: "",
        paused = parcel?.readInt() == 1,
        completed = parcel?.readInt() == 1,
        countDown = parcel?.readInt() == 1,
        dateTime = DateTime(parcel?.readString()),
        pausedDateTime = DateTime(parcel?.readString())
    ) {
        id = parcel?.readLong() ?: 0L
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeInt(if (paused) 1 else 0)
        dest.writeInt(if (completed) 1 else 0)
        dest.writeInt(if (countDown) 1 else 0)
        dest.writeString(dateTime.toString())
        dest.writeString(pausedDateTime?.toString())
        dest.writeLong(id)
    }
}