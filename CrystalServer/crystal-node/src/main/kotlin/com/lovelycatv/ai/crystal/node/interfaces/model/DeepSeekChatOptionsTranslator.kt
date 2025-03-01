package com.lovelycatv.ai.crystal.node.interfaces.model

import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.node.interfaces.model.base.ChatOptions2SpringAIOptionsTranslator
import org.springframework.ai.openai.OpenAiChatOptions

/**
 * @author lovelycat
 * @since 2025-03-01 16:03
 * @version 1.0
 */
class DeepSeekChatOptionsTranslator : ChatOptions2SpringAIOptionsTranslator<DeepSeekChatOptions, OpenAiChatOptions> {
    override fun translate(original: DeepSeekChatOptions?): OpenAiChatOptions {
        return OpenAiChatOptions.builder().apply {
            original?.modelName?.let {
                this.model(it)
            }
            original?.temperature.let {
                this.temperature(it)
            }
        }.build()
    }
}