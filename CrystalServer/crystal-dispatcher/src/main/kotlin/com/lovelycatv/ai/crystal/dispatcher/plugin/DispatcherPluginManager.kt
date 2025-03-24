package com.lovelycatv.ai.crystal.dispatcher.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessageMixIn
import com.lovelycatv.ai.crystal.common.data.message.ClientConnectedMessage
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeRequestMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import java.io.File

/**
 * @author lovelycat
 * @since 2025-03-02 23:00
 * @version 1.0
 */
object DispatcherPluginManager {
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

    val pluginsDir = File(File("").absolutePath, "plugins")

    private val _registeredPlugins: MutableList<AbstractDispatcherPlugin> = mutableListOf()
    val registeredPlugins: List<AbstractDispatcherPlugin> get() = this._registeredPlugins

    fun addRegisteredPlugin(plugin: AbstractDispatcherPlugin) {
        this._registeredPlugins.add(plugin)
    }

    val chatOptionsBuilders = this.registeredPlugins.flatMap { it.chatOptionsBuilders }
}