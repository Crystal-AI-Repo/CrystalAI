package com.lovelycatv.ai.crystal.dispatcher.config

import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.openapi.plugin.ChatOptionsBuilder
import com.lovelycatv.ai.crystal.openapi.plugin.EmbeddingOptionsBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-03-19 23:16
 * @version 1.0
 */
@Configuration
class DispatcherConfig {
    @Bean
    fun defaultChatOptionsBuilders(): List<ChatOptionsBuilder<*>> {
        return listOf(
            ChatOptionsBuilder<OllamaChatOptions>("ollama") { modelName ->
                OllamaChatOptions(modelName = modelName, temperature = null)
            },
            ChatOptionsBuilder<DeepSeekChatOptions>("deepseek") { modelName ->
                DeepSeekChatOptions(modelName = modelName, temperature = null)
            }
        )
    }

    @Bean
    fun defaultEmbeddingOptionsBuilders(): List<EmbeddingOptionsBuilder<*>> {
        return listOf(
            EmbeddingOptionsBuilder<OllamaEmbeddingOptions>("ollama") { modelName ->
                OllamaEmbeddingOptions(modelName = modelName)
            }
        )
    }
}