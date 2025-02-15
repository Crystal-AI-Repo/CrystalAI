package com.lovelycatv.ai.crystal.node.service.impl

import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.service.OllamaChatService
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

}