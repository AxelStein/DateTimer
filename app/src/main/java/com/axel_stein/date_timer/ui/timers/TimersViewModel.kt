package com.axel_stein.date_timer.ui.timers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axel_stein.date_timer.data.room.model.Timer
import org.joda.time.DateTime

class TimersViewModel : ViewModel() {
    private val items = MutableLiveData<List<Timer>>()
    val itemsLiveData: LiveData<List<Timer>> = items

    init {
        val dt = DateTime(2021, 2, 15, 14, 30)
        items.value = listOf(
            Timer("Timer 1", dt).also { it.id = 1 },
            Timer("Timer 2", dt).also { it.id = 2 },
            Timer("Timer 3", dt, true).also { it.id = 3 },
            Timer("Timer 4", dt).also { it.id = 4 },
            Timer("Timer 5", dt, paused = true, completed = true).also { it.id = 5 },
        )
    }

    fun pauseTimer(timer: Timer) {
        val newItems = mutableListOf<Timer>()
        items.value?.forEach { item ->
            newItems.add(
                Timer(item.title, item.dateTime, item.paused, item.completed)
                    .apply {
                        this.id = item.id
                        if (this.id == timer.id) {
                            this.paused = !paused
                        }
                    }
            )
        }
        this.items.postValue(newItems)
    }
}