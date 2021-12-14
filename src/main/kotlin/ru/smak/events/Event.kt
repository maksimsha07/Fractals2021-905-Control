package ru.smak.events

class Event<T> {
    private val listeners = mutableListOf<(Event<T>, T) -> Unit>()

    fun add(listener: (Event<T>, T) -> Unit) {
        listeners.add(listener)
    }

    fun remove(listener: (Event<T>, T) -> Unit) {
        listeners.remove(listener)
    }

    fun removeAll() {
        listeners.clear()
    }

    operator fun invoke(obj: T) {
        listeners.forEach { it(this, obj) }
    }
}