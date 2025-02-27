package com.lovelycatv.ai.crystal.dispatcher.task

/**
 * @author lovelycat
 * @since 2025-02-28 00:37
 * @version 1.0
 */
abstract class AbstractTask(
    val taskId: String,
    val timeout: Long
)