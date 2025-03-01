package com.lovelycatv.ai.crystal.node.service.chat

import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-02-15 16:06
 * @version 1.0
 */
@Service
class OllamaChatServiceImpl(
    private val nodeConfiguration: NodeConfiguration
) : OllamaChatService() {
    override fun buildChatModel(): OllamaChatModel {
        return OllamaChatModel.builder()
            .ollamaApi(OllamaApi(nodeConfiguration.ollama.baseUrl))
            .defaultOptions(
                this.translate(OllamaChatOptions(
                    modelName = nodeConfiguration.ollama.defaultModel,
                    temperature = nodeConfiguration.ollama.defaultTemperature
                ))
            )
            .build()
    }
}