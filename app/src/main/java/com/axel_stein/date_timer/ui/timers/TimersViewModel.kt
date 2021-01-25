package com.axel_stein.date_timer.ui.timers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.data.AppSettings.TimersSort.DATE
import com.axel_stein.date_timer.data.AppSettings.TimersSort.TITLE
import com.axel_stein.date_timer.data.room.dao.TimerDao
import com.axel_stein.date_timer.data.room.model.Timer
import com.axel_stein.date_timer.ui.App
import com.axel_stein.date_timer.utils.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import javax.inject.Inject

class TimersViewModel : ViewModel() {
    private lateinit var dao: TimerDao
    private val disposables = CompositeDisposable()

    private val items = MutableLiveData<List<Timer>>()
    val itemsLiveData: LiveData<List<Timer>> = items

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private lateinit var settings: AppSettings

    init {
        App.appComponent.inject(this)
        loadItems()
    }

    fun showCompleted(show: Boolean) {
        settings.setShowCompletedTimers(show)
        loadItems()
    }

    fun sortByTitle() {
        settings.sortTimersByTitle()
        loadItems()
    }

    fun sortByDate() {
        settings.sortTimersByDate()
        loadItems()
    }

    private fun loadItems() {
        val getItems = if (settings.showCompletedTimers()) {
            dao.getAll()
        } else {
            dao.getNotCompleted()
        }

        disposables.clear()
        disposables.add(
            getItems.subscribe({
                items.postValue(sortItems(it))
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_loading))
            })
        )
    }

    private fun sortItems(list: List<Timer>): List<Timer> {
        return when (settings.getTimersSort()) {
            TITLE -> list.sortedBy { it.title }
            DATE -> list.sortedByDescending { it.dateTime }
        }
    }

    @Inject
    fun inject(dao: TimerDao, settings: AppSettings) {
        this.dao = dao
        this.settings = settings
    }

    fun pauseTimer(timer: Timer) {
        dao.setPaused(timer.id, !timer.paused)
            .subscribeOn(io())
            .subscribe()
    }

    fun completeTimer(timer: Timer) {
        if (!timer.completed) {
            dao.setCompleted(timer.id, true)
                .subscribeOn(io())
                .subscribe()
        }
    }

    override fun onCleared() {
        disposables.clear()
    }
}