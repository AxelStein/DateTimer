package com.axel_stein.date_timer.ui.timers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.date_timer.data.room.dao.TimerDao
import com.axel_stein.date_timer.data.room.model.Timer
import com.axel_stein.date_timer.ui.App
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class TimersViewModel : ViewModel() {
    private lateinit var dao: TimerDao
    private val disposables = CompositeDisposable()

    private val items = MutableLiveData<List<Timer>>()
    val itemsLiveData: LiveData<List<Timer>> = items

    init {
        App.appComponent.inject(this)
        disposables.add(
            dao.getAll()
                .observeOn(mainThread())
                .subscribe({
                    items.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    @Inject
    fun setDao(dao: TimerDao) {
        this.dao = dao
    }

    fun pauseTimer(timer: Timer) {
        dao.setPaused(timer.id, !timer.paused)
            .subscribeOn(io())
            .subscribe()
    }

    override fun onCleared() {
        disposables.clear()
    }
}