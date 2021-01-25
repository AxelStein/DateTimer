package com.axel_stein.date_timer.ui.edit_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.room.dao.TimerDao
import com.axel_stein.date_timer.data.room.model.Timer
import com.axel_stein.date_timer.ui.App
import com.axel_stein.date_timer.utils.Event
import com.axel_stein.date_timer.utils.get
import com.axel_stein.date_timer.utils.getOrDefault
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
import javax.inject.Inject

class EditTimerViewModel(private val id: Long = 0L, private val state: SavedStateHandle) : ViewModel() {
    private var timerData = MutableLiveData<Timer>()
    val timerLiveData: LiveData<Timer> = timerData

    private val errorTitleEmpty = MutableLiveData<Boolean>()
    val errorTitleEmptyLiveData: LiveData<Boolean> = errorTitleEmpty

    private val errorDateTime = MutableLiveData<Int>()
    val errorDateTimeLiveData: LiveData<Int> = errorDateTime

    private val showMessage = MutableLiveData<Event<Int>>()
    val showMessageLiveData: LiveData<Event<Int>> = showMessage

    private val actionFinish = MutableLiveData<Event<Boolean>>()
    val actionFinishLiveData: LiveData<Event<Boolean>> = actionFinish

    private lateinit var dao: TimerDao

    init {
        App.appComponent.inject(this)
        loadData()
    }

    @Inject
    fun setDao(dao: TimerDao) {
        this.dao = dao
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        val timer = timerData.getOrDefault(Timer())
        val date = timer.dateTime.toMutableDateTime()
        date.year = year
        date.monthOfYear = month
        date.dayOfMonth = dayOfMonth
        timer.dateTime = date.toDateTime()
        checkDateTime()
    }

    fun setTime(hourOfDay: Int, minuteOfHour: Int) {
        val timer = timerData.getOrDefault(Timer())
        val date = timer.dateTime.toMutableDateTime()
        date.hourOfDay = hourOfDay
        date.minuteOfHour = minuteOfHour
        timer.dateTime = date.toDateTime()
        checkDateTime()
    }

    private fun checkDateTime() {
        val timer = timerData.get()
        val dateTime = timer.dateTime.withSecondOfMinute(0).withMillisOfSecond(0)
        val now = DateTime.now().withSecondOfMinute(0).withMillisOfSecond(0)
        if (timer.countDown) {
            if (dateTime.isBefore(now)) {
                errorDateTime.value = R.string.error_date_time_later
            } else {
                errorDateTime.value = null
            }
        } else {
            if (dateTime.isAfter(now)) {
                errorDateTime.value = R.string.error_date_time_earlier
            } else {
                errorDateTime.value = null
            }
        }
    }

    fun getCurrentDateTime() = timerData.value?.dateTime ?: DateTime()

    fun setTitle(title: String) {
        timerData.getOrDefault(Timer()).title = title
        state["title"] = title
        if (title.isNotBlank()) {
            errorTitleEmpty.value = false
        }
    }

    fun setCountDown(value: Boolean) {
        timerData.getOrDefault(Timer()).countDown = value
        state["count_down"] = value
        checkDateTime()
    }

    private fun loadData() {
        if (id == 0L) timerData.value = Timer()
        else dao.getById(id)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({
                timerData.value = it
            }, {
                it.printStackTrace()
                showMessage.value = Event(R.string.error_loading)
            })
    }

    fun save() {
        checkDateTime()
        when {
            timerData.get().title.isBlank() -> errorTitleEmpty.value = true
            errorDateTime.value != null -> {}
            else -> {
                Completable.fromAction {
                    val timer = timerData.get()
                    timer.dateTime = timer.dateTime
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0)
                    timer.completed = false
                    dao.upsert(timer)
                }.subscribeOn(io()).subscribe({
                    showMessage.postValue(Event(R.string.msg_timer_saved))
                    actionFinish.postValue(Event())
                }, {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_saving))
                })
            }
        }
    }

    fun delete() {
        if (id != 0L) dao.deleteById(id)
            .subscribeOn(io())
            .subscribe(
                {
                    showMessage.postValue(Event(R.string.msg_timer_deleted))
                    actionFinish.postValue(Event())
                },
                {
                    it.printStackTrace()
                    showMessage.postValue(Event(R.string.error_deleting))
                }
            )
    }
}