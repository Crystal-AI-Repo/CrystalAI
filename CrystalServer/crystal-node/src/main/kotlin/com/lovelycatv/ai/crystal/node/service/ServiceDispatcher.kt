package com.lovelycatv.ai.crystal.node.service

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.node.api.dispatcher.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.api.dispatcher.EmbeddingServiceDispatcher
import com.lovelycatv.ai.crystal.node.data.AbstractChatResult
import com.lovelycatv.ai.crystal.node.data.AbstractEmbeddingResult
import com.lovelycatv.ai.crystal.node.plugin.NodePluginManager
import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractChatService
import com.lovelycatv.ai.crystal.node.service.embedding.base.AbstractEmbeddingService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-03-28 19:44
 * @version 1.0
 */
@Component
class ServiceDispatcher(
    private val chatServiceDispatchers: List<ChatServiceDispatcher>,
    private val embeddingServiceDispatchers: List<EmbeddingServiceDispatcher>
) {
    fun determineEmbeddingService(optionClazz: KClass<out AbstractEmbeddingOptions>) = null.run {
        var service: AbstractEmbeddingService<AbstractEmbeddingOptions, AbstractEmbeddingResult>? = null
        (embeddingServiceDispatchers + NodePluginManager.registeredPlugins.flatMap { it.embeddingServiceDispatchers }).forEach {
            if (service == null) {
                service = it.getService(optionClazz)
            } else {
                return@forEach
            }
        }
        service
    }

    fun determineChatService(optionClazz: KClass<out AbstractChatOptions>) = null.run {
        var service: AbstractChatService<AbstractChatOptions, AbstractChatResult, Any>? = null
        (chatServiceDispatchers + NodePluginManager.registeredPlugins.flatMap { it.chatServiceDispatchers }).forEach {
            if (service == null) {
                service = it.getService(optionClazz)
            } else {
                return@forEach
            }
        }
        service
    }
}