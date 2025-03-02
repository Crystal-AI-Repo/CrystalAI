package com.lovelycatv.ai.crystal.node.plugin

import org.springframework.context.ApplicationContext

/**
 * @author lovelycat
 * @since 2025-03-02 15:52
 * @version 1.0
 */
abstract class NodePlugin {
    private val pluginName: String = this::class.qualifiedName!!

    open fun onLoad() {

    }

    open fun onEnabled() {

    }

    open fun onDisabled() {

    }

    fun getConfig(): Map<String, Any?> {
        return PluginManager.registeredPlugins[pluginName]!!.rawPluginConfig
    }
}