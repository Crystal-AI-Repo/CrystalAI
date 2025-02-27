package com.lovelycatv.ai.crystal.dispatcher.task

import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-02-28 00:30
 * @version 1.0
 */
abstract class AbstractChatTask(
    taskId: String,
    timeout: Long
) : AbstractTask(taskId, timeout)