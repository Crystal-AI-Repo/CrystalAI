package com.lovelycatv.ai.crystal.common.data

/**
 * @author lovelycat
 * @since 2025-02-27 01:50
 * @version 1.0
 */
abstract class LiveData<T> : Observable<T>() {
    abstract fun get(): T
}