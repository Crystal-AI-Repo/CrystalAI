package com.lovelycatv.crystal.plugin.data

/**
 * @author lovelycat
 * @since 2025-03-02 16:09
 * @version 1.0
 */
open class RawLoadedPlugin(
    val metadata: PluginMetadata,
    val classLoader: ClassLoader
)