package com.lovelycatv.ai.crystal.common.data

/**
 * @author lovelycat
 * @since 2025-02-27 01:48
 * @version 1.0
 */
data class MutableLiveData<T>(var data: T) : LiveData<T> {
    override fun get(): T {
        return this.data
    }

    fun set(data: T) {
        this.data = data
    }
}