package com.lovelycatv.crystal.plugin.nvidia.node.interfaces

import com.lovelycatv.ai.crystal.node.api.interfaces.model.ChatOptions2SpringAIOptionsTranslator
import com.lovelycatv.crystal.plugin.nvidia.common.message.NvidiaChatOptions
import org.springframework.ai.openai.OpenAiChatOptions

/**
 * @author lovelycat
 * @since 2025-03-02 16:47
 * @version 1.0
 */
class NvidiaChatOptionsTranslator :
    ChatOptions2SpringAIOptionsTranslator<NvidiaChatOptions, OpenAiChatOptions> {

    override fun translate(original: NvidiaChatOptions?): OpenAiChatOptions {
        return OpenAiChatOptions().apply {
            this.model = original?.modelName
        }
    }
}