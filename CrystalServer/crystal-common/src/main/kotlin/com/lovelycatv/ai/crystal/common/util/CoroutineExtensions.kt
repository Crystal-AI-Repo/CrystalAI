package com.lovelycatv.ai.crystal.common.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author lovelycat
 * @since 2025-02-26 19:12
 * @version 1.0
 */
class CoroutineExtensions private constructor()

suspend fun <T> runInIO(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, block)