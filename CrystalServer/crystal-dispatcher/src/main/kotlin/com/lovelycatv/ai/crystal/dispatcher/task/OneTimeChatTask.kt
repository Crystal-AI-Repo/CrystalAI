package com.lovelycatv.ai.crystal.dispatcher.task

import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-02-27 23:12
 * @version 1.0
 */
class OneTimeChatTask(
    val options: OllamaChatOptions?,
    val prompts: List<PromptMessage>,
    timeout: Long = 0L,
    taskId: String = UUID.randomUUID().toString()
) : AbstractChatTask(taskId, timeout)