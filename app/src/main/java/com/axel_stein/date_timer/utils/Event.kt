package com.axel_stein.date_timer.utils

@Suppress("unused")
class Event<T>(private val content: T? = null) {
    private var handled = false

    fun getContent(): T? {
        return if (handled) {
            null
        } else {
            handleEvent()
            content
        }
    }

    fun handleEvent() {
        handled = true
    }

    fun isHandled() = handled
}