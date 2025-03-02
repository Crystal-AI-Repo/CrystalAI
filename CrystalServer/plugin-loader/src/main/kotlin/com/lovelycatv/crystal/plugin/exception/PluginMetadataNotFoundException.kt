package com.lovelycatv.crystal.plugin.exception

/**
 * @author lovelycat
 * @since 2025-03-02 15:11
 * @version 1.0
 */
class PluginMetadataNotFoundException(pluginFileName: String)
    : RuntimeException("Could not find plugin metadata file (plugin.yml) in $pluginFileName")