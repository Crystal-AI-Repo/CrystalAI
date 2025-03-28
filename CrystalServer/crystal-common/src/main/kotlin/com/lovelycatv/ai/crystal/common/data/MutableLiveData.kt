package com.lovelycatv.ai.crystal.common.data

/**
 * @author lovelycat
 * @since 2025-02-27 01:48
 * @version 1.0
 */
data class MutableLiveData<T>(private var data: T) : LiveData<T>() {
    override fun get(): T {
        return this.data
    }

    fun post(data: T) {
        val old = this.data
        this.data = data
        super.publish(old, data)
    }
}