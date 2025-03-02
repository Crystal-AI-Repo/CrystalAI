package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.node.data.AbstractEmbeddingResult
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.node.data.AbstractChatResult
import com.lovelycatv.ai.crystal.node.task.AbstractTask
import com.lovelycatv.ai.crystal.node.queue.InMemoryTaskQueue
import com.lovelycatv.ai.crystal.node.service.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.service.EmbeddingServiceDispatcher
import com.lovelycatv.ai.crystal.node.service.chat.DeepSeekChatService
import com.lovelycatv.ai.crystal.node.service.chat.OllamaChatService
import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractChatService
import com.lovelycatv.ai.crystal.node.service.embedding.OllamaEmbeddingService
import com.lovelycatv.ai.crystal.node.service.embedding.base.AbstractEmbeddingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-02-28 18:13
 * @version 1.0
 */
@Suppress("UNCHECKED_CAST")
@Configuration
class NodeConfig(
    private val ollamaChatService: OllamaChatService,
    private val deepSeekChatService: DeepSeekChatService,
    private val ollamaEmbeddingService: OllamaEmbeddingService
) {
    @Bean
    fun taskQueue(): InMemoryTaskQueue<AbstractTask> {
        return InMemoryTaskQueue()
    }

    @Bean
    fun defaultEmbeddingServiceDispatcher(): List<EmbeddingServiceDispatcher>? {
        return listOf(
            object : EmbeddingServiceDispatcher {
                override fun getService(options: KClass<out AbstractEmbeddingOptions>): AbstractEmbeddingService<AbstractEmbeddingOptions, AbstractEmbeddingResult>? {
                    return when (options) {
                        OllamaEmbeddingOptions::class -> {
                            ollamaEmbeddingService
                        }
                        else -> null
                    } as? AbstractEmbeddingService<AbstractEmbeddingOptions, AbstractEmbeddingResult>?
                }

            }
        )
    }

    @Bean
    fun defaultChatServiceDispatcher(): List<ChatServiceDispatcher> {
        return listOf(
            object : ChatServiceDispatcher {
                override fun getService(options: KClass<out AbstractChatOptions>): AbstractChatService<AbstractChatOptions, AbstractChatResult, Any>? {
                    return when (options) {
                        OllamaChatOptions::class -> ollamaChatService
                        DeepSeekChatOptions::class -> deepSeekChatService
                        else -> null
                    } as? AbstractChatService<AbstractChatOptions, AbstractChatResult, Any>?
                }

            }
        )
    }
}