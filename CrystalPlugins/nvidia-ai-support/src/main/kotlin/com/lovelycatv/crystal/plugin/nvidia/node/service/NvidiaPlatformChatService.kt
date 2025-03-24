package com.lovelycatv.crystal.plugin.nvidia.node.service

import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractSpringAIChatService
import com.lovelycatv.crystal.plugin.nvidia.node.interfaces.NvidiaChatOptionsTranslator
import com.lovelycatv.crystal.plugin.nvidia.common.message.NvidiaChatOptions
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions

/**
 * @author lovelycat
 * @since 2025-03-02 15:29
 * @version 1.0
 */
abstract class NvidiaPlatformChatService
    : AbstractSpringAIChatService<OpenAiChatModel, OpenAiChatOptions, NvidiaChatOptions>(
    translatorDelegate = NvidiaChatOptionsTranslator()
)