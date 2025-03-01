package com.lovelycatv.ai.crystal.dispatcher.task

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-02-28 00:30
 * @version 1.0
 */
abstract class AbstractChatTask<OPTIONS: AbstractChatOptions>(
    val options: OPTIONS,
    val prompts: List<PromptMessage>,
    timeout: Long = 0L,
    taskId: String = UUID.randomUUID().toString(),
) : AbstractTask(taskId, timeout)