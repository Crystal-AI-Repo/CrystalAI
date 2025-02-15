package com.lovelycatv.ai.crystal.common.util

/**
 * @author lovelycat
 * @since 2025-02-15 19:08
 * @version 1.0
 */
class ExceptionExtensions private constructor()

fun <R> catchException(
    printStackTrace: Boolean = true,
    onException: ((e: Exception) -> Unit)? = null,
    returnOnException: R? = null,
    fx: () -> R
): R? {
    return try {
        fx()
    } catch (e: Exception) {
        if (printStackTrace) {
            e.printStackTrace()
        }
        onException?.invoke(e)
        returnOnException
    }
}