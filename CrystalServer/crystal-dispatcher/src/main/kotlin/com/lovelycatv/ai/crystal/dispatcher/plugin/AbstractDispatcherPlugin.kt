package com.lovelycatv.ai.crystal.dispatcher.plugin

import com.lovelycatv.ai.crystal.openapi.plugin.ChatOptionsBuilder
import com.lovelycatv.ai.crystal.openapi.plugin.EmbeddingOptionsBuilder
import com.lovelycatv.crystal.plugin.AbstractPlugin
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-03-02 22:41
 * @version 1.0
 */
abstract class AbstractDispatcherPlugin : AbstractPlugin() {
    private lateinit var pluginContext: ApplicationContext

    val chatOptionsBuilders: MutableList<ChatOptionsBuilder<*>> = mutableListOf()

    val embeddingOptionsBuilders: MutableList<EmbeddingOptionsBuilder<*>> = mutableListOf()

    fun setPluginContext(context: ApplicationContext) {
        this.pluginContext = context
    }

    fun getPluginContext() = this.pluginContext

    fun <T: Any> getBeans(clazz: KClass<T>) = this.pluginContext.getBeansOfType(clazz.java).values.filterNotNull()
}