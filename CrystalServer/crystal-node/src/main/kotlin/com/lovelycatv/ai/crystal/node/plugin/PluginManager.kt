package com.lovelycatv.ai.crystal.node.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.data.message.*
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeRequestMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.node.service.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.service.EmbeddingServiceDispatcher

/**
 * @author lovelycat
 * @since 2025-03-02 15:54
 * @version 1.0
 */
object PluginManager {
    val objectMapper = ObjectMapper().apply {
        addMixIn(AbstractMessage::class.java, AbstractMessageMixIn::class.java)
        registerSubtypes(
            AuthorizeRequestMessage::class.java,
            AuthorizeResponseMessage::class.java,
            ClientConnectedMessage::class.java,
            PromptMessage::class.java,
            OllamaChatOptions::class.java,
            DeepSeekChatOptions::class.java,
            OllamaEmbeddingOptions::class.java,
            ChatResponseMessage::class.java,
            EmbeddingResponseMessage::class.java
        )
    }

    private val pluginLoader = PluginLoader()

    private val _registeredPlugins: MutableMap<String, LoadedPlugin> = mutableMapOf()
    val registeredPlugins: Map<String, LoadedPlugin> get() = this._registeredPlugins

    fun registerPlugin(pluginJarPath: String) {
        val raw = pluginLoader.loadPlugin(pluginJarPath)
        val metadata = raw.metadata
        val pluginContext = raw.context
        val classLoader = raw.classLoader
        val pluginConfig = raw.rawPluginConfig

        val pluginInstance = classLoader.loadClass(metadata.main).getDeclaredConstructor().newInstance() as NodePlugin
        pluginInstance.onLoad()

        val messageTypeRegistries = pluginContext.getBeansOfType(AbstractMessageTypeRegistry::class.java)
        messageTypeRegistries.values.filterNotNull().forEach {
            it.registerTypes(objectMapper)
        }

        val chatServiceDispatchers = pluginContext.getBeansOfType(ChatServiceDispatcher::class.java).values.filterNotNull()
        val embeddingServiceDispatchers = pluginContext.getBeansOfType(EmbeddingServiceDispatcher::class.java).values.filterNotNull()

        pluginInstance.onEnabled()

        this._registeredPlugins[metadata.main] = LoadedPlugin(
            pluginInstance,
            embeddingServiceDispatchers,
            chatServiceDispatchers,
            metadata, pluginContext, classLoader, pluginConfig
        )
    }

    val chatServiceDispatchers get() = this.registeredPlugins.values.flatMap { it.chatServiceDispatchers }

    val embeddingServiceDispatchers get() = this.registeredPlugins.values.flatMap { it.embeddingServiceDispatchers }
}