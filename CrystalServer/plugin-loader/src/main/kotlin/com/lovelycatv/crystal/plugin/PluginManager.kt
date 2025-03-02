package com.lovelycatv.crystal.plugin

import com.lovelycatv.crystal.plugin.api.PluginLoaderHook
import com.lovelycatv.crystal.plugin.data.LoadedPlugin
import com.lovelycatv.crystal.plugin.data.RawLoadedPlugin
import java.io.File

/**
 * @author lovelycat
 * @since 2025-03-02 15:54
 * @version 1.0
 */
object PluginManager {
    val pluginsDir = File(System.getProperty("user.dir"), "plugins")

    private val pluginLoader = PluginLoader()

    private val _registeredRawPlugins: MutableMap<String, RawLoadedPlugin> = mutableMapOf()
    val registeredRawPlugins: Map<String, RawLoadedPlugin> get() = this._registeredRawPlugins

    private val _registeredPlugins: MutableMap<String, LoadedPlugin> = mutableMapOf()
    val registeredPlugins: Map<String, LoadedPlugin> get() = _registeredPlugins

    fun registerPlugin(pluginJarPath: String) {
        val raw = pluginLoader.loadPlugin(pluginJarPath)
        val metadata = raw.metadata
        val classLoader = raw.classLoader

        PluginLoaderHook.onPluginLoadedListeners.forEach {
            it.onLoaded(raw)
        }

        _registeredRawPlugins[metadata.main] = raw

        val pluginInstance = classLoader.loadClass(metadata.main).getDeclaredConstructor().newInstance() as AbstractPlugin
        pluginInstance.onLoad()

        PluginLoaderHook.onPluginPostEnabledListeners.forEach {
            it.beforeEnable(raw)
        }

        pluginInstance.onEnabled()

        val loadedPlugin = LoadedPlugin(
            pluginInstance,
            PluginLoader.getPluginConfig(File(File(pluginsDir, metadata.name), "config.yml").absolutePath),
            metadata, classLoader,
        )

        _registeredRawPlugins.remove(metadata.main)
        _registeredPlugins[metadata.main] = loadedPlugin

        PluginLoaderHook.onPluginEnabledListeners.forEach {
            it.onEnabled(loadedPlugin)
        }
    }

    fun unregisterPlugin(plugin: AbstractPlugin) {
        _registeredPlugins[plugin.pluginClassName]?.pluginInstance?.onDisabled()
    }
}