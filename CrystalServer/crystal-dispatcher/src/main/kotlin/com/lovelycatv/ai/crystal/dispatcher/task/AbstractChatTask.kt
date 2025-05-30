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
    options: OPTIONS,
    prompts: List<PromptMessage>,
    timeout: Long,
    taskId: String = UUID.randomUUID().toString(),
) : AbstractModelTask<OPTIONS>(options, prompts, timeout, taskId)