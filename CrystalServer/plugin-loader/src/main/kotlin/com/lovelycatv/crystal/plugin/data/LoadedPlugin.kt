package com.lovelycatv.crystal.plugin.data

import com.lovelycatv.crystal.plugin.AbstractPlugin
import com.lovelycatv.crystal.plugin.config.YAMLConfiguration

/**
 * @author lovelycat
 * @since 2025-03-02 16:56
 * @version 1.0
 */
class LoadedPlugin(
    val pluginInstance: AbstractPlugin,
    val pluginConfig: YAMLConfiguration,
    metadata: PluginMetadata,
    classLoader: ClassLoader
) : RawLoadedPlugin(metadata, classLoader)