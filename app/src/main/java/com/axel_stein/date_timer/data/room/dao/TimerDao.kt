package com.axel_stein.date_timer.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.axel_stein.date_timer.data.room.model.Timer
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class TimerDao : BaseDao<Timer>() {
    @Query("UPDATE timers SET paused = :paused WHERE id = :id")
    abstract fun setPaused(id: Long, paused: Boolean): Completable

    @Query("UPDATE timers SET completed = :completed WHERE id = :id")
    abstract fun setCompleted(id: Long, completed: Boolean): Completable

    @Query("delete FROM timers WHERE id = :id")
    abstract fun deleteById(id: Long): Completable

    @Query("SELECT * FROM timers WHERE id = :id")
    abstract fun getById(id: Long): Single<Timer>

    @Query("SELECT * FROM timers")
    abstract fun getAll(): Flowable<List<Timer>>

    @Query("SELECT * FROM timers WHERE completed = 0")
    abstract fun getNotCompleted(): Flowable<List<Timer>>

    @Query("SELECT * FROM timers WHERE completed = 0 AND paused = 0 AND count_down = 1")
    abstract fun getActiveCountDownTimers(): List<Timer>
}