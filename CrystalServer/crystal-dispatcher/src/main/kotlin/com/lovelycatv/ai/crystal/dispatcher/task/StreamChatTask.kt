package com.lovelycatv.ai.crystal.dispatcher.task

import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.chat.options.AbstractChatOptions
import java.util.*

/**
 * @author lovelycat
 * @since 2025-02-28 21:58
 * @version 1.0
 */
class StreamChatTask<OPTIONS: AbstractChatOptions>(
    options: OPTIONS?,
    prompts: List<PromptMessage>,
    timeout: Long = 0L,
    taskId: String = UUID.randomUUID().toString()
) : AbstractChatTask<OPTIONS>(options, prompts, timeout, taskId)