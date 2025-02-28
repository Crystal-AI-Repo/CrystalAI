package com.lovelycatv.ai.crystal.node.service.chat

import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractSpringAIChatService
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaOptions

/**
 * @author lovelycat
 * @since 2025-02-15 16:05
 * @version 1.0
 */
abstract class OllamaChatService : AbstractSpringAIChatService<OllamaChatModel, OllamaOptions, OllamaChatOptions>()