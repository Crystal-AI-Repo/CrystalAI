package com.lovelycatv.crystal.plugin.nvidia.dispatcher

import com.lovelycatv.ai.crystal.dispatcher.plugin.AbstractDispatcherPlugin

/**
 * @author lovelycat
 * @since 2025-03-23 20:50
 * @version 1.0
 */
class DispatcherMainClass : AbstractDispatcherPlugin() {
    companion object {
        var plugin: DispatcherMainClass? = null
    }

    override fun onEnabled() {
        super.onEnabled()

        saveDefaultConfig()

        logger.info("Nvidia AI Supports for dispatcher is enabled.")

        plugin = this
    }
}