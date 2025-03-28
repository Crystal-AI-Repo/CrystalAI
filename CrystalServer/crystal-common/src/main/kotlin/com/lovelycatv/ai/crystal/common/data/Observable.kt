package com.lovelycatv.ai.crystal.common.data

/**
 * @author lovelycat
 * @since 2025-03-29 03:25
 * @version 1.0
 */
abstract class Observable<T> {
    private val _listeners: MutableSet<Listener<T>> = mutableSetOf()

    fun observe(listener: Listener<T>) {
        this._listeners.add(listener)
    }

    fun removeObserver(listener: Listener<T>) {
        this._listeners.remove(listener)
    }

    fun publish(old: T, new: T) {
        this._listeners.forEach {
            it.received(old, new)
        }
    }

    interface Listener<T> {
        fun received(old: T, new: T)
    }
}