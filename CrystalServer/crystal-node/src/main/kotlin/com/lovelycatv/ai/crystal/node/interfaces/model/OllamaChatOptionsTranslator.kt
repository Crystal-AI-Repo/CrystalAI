package com.lovelycatv.ai.crystal.node.interfaces.model

import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.node.api.interfaces.model.ChatOptions2SpringAIOptionsTranslator
import org.springframework.ai.ollama.api.OllamaOptions

/**
 * @author lovelycat
 * @since 2025-03-01 16:03
 * @version 1.0
 */
class OllamaChatOptionsTranslator : ChatOptions2SpringAIOptionsTranslator<OllamaChatOptions, OllamaOptions> {
    override fun translate(original: OllamaChatOptions?): OllamaOptions {
        return OllamaOptions.builder().apply {
            original?.modelName?.let {
                this.model(it)
            }
            original?.temperature.let {
                this.temperature(it)
            }
        }.build()
    }
}