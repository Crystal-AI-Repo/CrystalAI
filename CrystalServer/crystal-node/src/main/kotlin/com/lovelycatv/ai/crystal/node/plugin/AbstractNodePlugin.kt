package com.lovelycatv.ai.crystal.node.plugin

import com.lovelycatv.ai.crystal.node.api.dispatcher.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.api.dispatcher.EmbeddingServiceDispatcher
import com.lovelycatv.ai.crystal.node.api.task.NodeChatTaskBuilder
import com.lovelycatv.ai.crystal.node.api.task.NodeEmbeddingTaskBuilder
import com.lovelycatv.crystal.plugin.AbstractPlugin
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-03-02 22:41
 * @version 1.0
 */
abstract class AbstractNodePlugin : AbstractPlugin() {
    private lateinit var pluginContext: ApplicationContext
    val chatServiceDispatchers: MutableList<ChatServiceDispatcher> = mutableListOf()
    val embeddingServiceDispatchers: MutableList<EmbeddingServiceDispatcher> = mutableListOf()

    val chatTaskBuilders: MutableList<NodeChatTaskBuilder<*>> = mutableListOf()
    val embeddingTaskBuilders: MutableList<NodeEmbeddingTaskBuilder<*>> = mutableListOf()

    fun setPluginContext(context: ApplicationContext) {
        this.pluginContext = context
    }

    fun getPluginContext() = this.pluginContext

    fun <T: Any> getBeans(clazz: KClass<T>) = this.pluginContext.getBeansOfType(clazz.java).values.filterNotNull()
}