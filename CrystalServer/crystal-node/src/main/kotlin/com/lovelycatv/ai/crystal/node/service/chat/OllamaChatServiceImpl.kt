package com.lovelycatv.ai.crystal.node.service.chat

import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaOptions
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
                OllamaOptions.builder().apply {
                    nodeConfiguration.ollama.defaultModel?.let {
                        this.model(it)
                    }
                    nodeConfiguration.ollama.defaultTemperature.let {
                        this.temperature(it)
                    }
                }.build()
            )
            .build()
    }

    override fun applyOptionsToSpringAIModelOptions(customOptions: OllamaChatOptions?): OllamaOptions {
        return OllamaOptions.builder().apply {
            customOptions?.modelName?.let {
                this.model(it)
            }
            customOptions?.temperature.let {
                this.temperature(it)
            }
        }.build()
    }
}