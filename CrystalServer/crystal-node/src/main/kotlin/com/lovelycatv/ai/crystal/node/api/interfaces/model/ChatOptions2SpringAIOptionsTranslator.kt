package com.lovelycatv.ai.crystal.node.api.interfaces.model

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import org.springframework.ai.chat.prompt.ChatOptions

/**
 * @author lovelycat
 * @since 2025-03-01 16:10
 * @version 1.0
 */
interface ChatOptions2SpringAIOptionsTranslator<CHAT_OPTIONS: AbstractChatOptions, MODEL_OPTIONS: ChatOptions>
    : ModelOptions2SpringAIOptionsTranslator<CHAT_OPTIONS, MODEL_OPTIONS>