package com.lovelycatv.ai.crystal.node.plugin

import org.springframework.context.ApplicationContext

/**
 * @author lovelycat
 * @since 2025-03-02 16:09
 * @version 1.0
 */
open class RawLoadedPlugin(
    val metadata: PluginMetadata,
    val context: ApplicationContext,
    val classLoader: ClassLoader,
    val rawPluginConfig: Map<String, Any?>
)