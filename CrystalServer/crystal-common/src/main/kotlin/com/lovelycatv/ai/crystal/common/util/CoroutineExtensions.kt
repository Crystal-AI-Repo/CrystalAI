package com.lovelycatv.ai.crystal.common.util

import kotlinx.coroutines.*
import kotlin.coroutines.Continuation

/**
 * @author lovelycat
 * @since 2025-02-26 19:12
 * @version 1.0
 */
class CoroutineExtensions private constructor()

suspend fun <T> runInIO(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, block)

suspend fun <T> suspendTimeoutCoroutine(maxWaitTimeMillis: Long, onTimeout: () -> Unit = {}, block: (Continuation<T>) -> Unit): T? {
    val startTime = System.currentTimeMillis()
    val scope = CoroutineScope(Dispatchers.IO)

    var continuation: CancellableContinuation<T>? = null

    scope.launch {
        while (true) {
            delay(100)
            if (continuation?.isCompleted == true) {
                this.cancel()
                return@launch
            } else if (System.currentTimeMillis() - startTime > maxWaitTimeMillis) {
                continuation!!.cancel()
                this.cancel()
                return@launch
            }
        }
    }

    return withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine {
                continuation = it
                block.invoke(it)
            }
        } catch (e: CancellationException) {
            null
        }
    }
}
