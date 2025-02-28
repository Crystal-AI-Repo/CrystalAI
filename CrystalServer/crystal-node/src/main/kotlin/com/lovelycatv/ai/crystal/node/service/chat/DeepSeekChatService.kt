package com.lovelycatv.ai.crystal.node.service.chat

import com.lovelycatv.ai.crystal.common.data.message.chat.options.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractSpringAIChatService
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions

/**
 * @author lovelycat
 * @since 2025-02-28 15:01
 * @version 1.0
 */
abstract class DeepSeekChatService : AbstractSpringAIChatService<OpenAiChatModel, OpenAiChatOptions, DeepSeekChatOptions>()