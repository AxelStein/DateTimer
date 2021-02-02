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
import com.axel_stein.date_timer.ui.reminder.ReminderScheduler
import com.axel_stein.date_timer.utils.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import javax.inject.Inject
import kotlin.collections.set

class TimersViewModel : ViewModel() {
    private lateinit var dao: TimerDao
    private val disposables = CompositeDisposable()

    private val items = MutableLiveData<MutableList<Timer>>()
    val itemsLiveData: LiveData<MutableList<Timer>> = items

    private val hiddenItems = mutableMapOf<Long, Timer>()

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private lateinit var settings: AppSettings
    private lateinit var reminderScheduler: ReminderScheduler

    init {
        App.appComponent.inject(this)
        loadItems()
    }

    fun showCompleted(show: Boolean) {
        settings.setShowCompletedTimers(show)
        loadItems()
    }

    fun showPaused(show: Boolean) {
        settings.setShowPausedTimers(show)
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
        val getItems = if (settings.showCompletedTimers()) dao.getAll() else dao.getNotCompleted()
        disposables.clear()

        getItems.subscribe({
            val list = if (!settings.showPausedTimers()) {
                it.filter { item -> !item.paused }
            } else {
                it
            }.filter { item ->
                !hiddenItems.containsKey(item.id)
            }
            items.postValue(sortItems(list))
        }, {
            it.printStackTrace()
            showMessage.postValue(Event(R.string.error_loading))
        }).also {
            disposables.add(it)
        }
    }

    private fun sortItems(list: List<Timer>): MutableList<Timer> {
        val ml = list.toMutableList()
        when (settings.getTimersSort()) {
            TITLE -> ml.sortBy { it.title }
            DATE -> ml.sortByDescending { it.dateTime }
        }
        return ml
    }

    @Inject
    fun inject(dao: TimerDao, settings: AppSettings, scheduler: ReminderScheduler) {
        this.dao = dao
        this.settings = settings
        this.reminderScheduler = scheduler
    }

    fun pauseTimer(timer: Timer) {
        val paused = !timer.paused
        val dt = if (paused) DateTime.now() else null
        dao.setPaused(timer.id, paused, dt)
            .subscribeOn(io())
            .subscribe({
                val copy = timer.copy()
                copy.id = timer.id
                copy.paused = paused
                if (paused) {
                    reminderScheduler.cancel(copy)
                } else {
                    reminderScheduler.schedule(copy)
                }
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error))
            }).also {
                disposables.add(it)
            }
    }

    fun completeTimer(timer: Timer) {
        if (!timer.completed) {
            dao.setCompleted(timer.id, true)
                .subscribeOn(io())
                .subscribe({}, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error))
                }).also {
                    disposables.add(it)
                }
        }
    }

    fun hideTimerById(id: Long) {
        items.value?.let {
            it.removeById(id)?.also { timer ->
                hiddenItems[id] = timer
            }
        }
    }

    fun showTimerById(id: Long) {
        if (hiddenItems.containsKey(id)) {
            val timer = hiddenItems.remove(id)
            if (timer != null) {
                items.value?.let {
                    it.add(timer)
                    items.value = sortItems(it)
                }
            }
        }
    }

    fun deleteTimerById(id: Long) {
        dao.deleteById(id)
            .subscribeOn(io())
            .subscribe({
                reminderScheduler.cancelById(id)
                hiddenItems.remove(id)
            }, {
                it.printStackTrace()
                showMessage.postValue(Event(R.string.error_deleting))
            }).also {
                disposables.add(it)
            }
    }

    fun getTimerByPosition(position: Int): Timer? = items.value?.get(position)

    override fun onCleared() {
        disposables.clear()
        hiddenItems.forEach {
            deleteTimerById(it.key)
        }
        hiddenItems.clear()
    }

    private fun MutableList<Timer>.removeById(id: Long): Timer? {
        val iterator = listIterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.id == id) {
                iterator.remove()
                return item
            }
        }
        return null
    }
}