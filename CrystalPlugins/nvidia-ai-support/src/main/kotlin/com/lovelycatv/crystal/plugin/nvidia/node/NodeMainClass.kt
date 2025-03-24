package com.lovelycatv.crystal.plugin.nvidia.node

import com.lovelycatv.ai.crystal.node.plugin.AbstractNodePlugin

/**
 * @author lovelycat
 * @since 2025-03-23 20:50
 * @version 1.0
 */
class NodeMainClass : AbstractNodePlugin() {
    companion object {
        var plugin: NodeMainClass? = null
    }

    override fun onEnabled() {
        super.onEnabled()

        saveDefaultConfig()

        logger.info("Nvidia AI Supports for node is enabled.")

        plugin = this
    }
}