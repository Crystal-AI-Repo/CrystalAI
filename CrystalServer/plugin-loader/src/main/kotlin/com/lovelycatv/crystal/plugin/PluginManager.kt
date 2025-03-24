package com.lovelycatv.crystal.plugin

import com.lovelycatv.crystal.plugin.api.PluginLoaderHook
import com.lovelycatv.crystal.plugin.data.LoadedPlugin
import com.lovelycatv.crystal.plugin.data.PluginMetadata
import com.lovelycatv.crystal.plugin.data.RawLoadedPlugin
import java.io.File

/**
 * @author lovelycat
 * @since 2025-03-02 15:54
 * @version 1.0
 */
object PluginManager {
    val pluginsDir = File(System.getProperty("user.dir"), "plugins")

    private val _registeredRawPlugins: MutableMap<String, RawLoadedPlugin> = mutableMapOf()
    val registeredRawPlugins: Map<String, RawLoadedPlugin> get() = this._registeredRawPlugins

    private val _registeredPlugins: MutableMap<String, LoadedPlugin> = mutableMapOf()
    val registeredPlugins: Map<String, LoadedPlugin> get() = _registeredPlugins

    /**
     * Register a plugin, if you want to do something when the plugin is loaded/enabled/disabled,
     * see hook functions in [PluginLoaderHook]
     *
     * @param T PluginMetadataPlugin or its subtype
     * @param pluginJarPath Plugin JAR file path
     * @param metadataClazz T.class
     * @param pluginMainClass Default plugin main class is using the config property: main.
     *                        You could customize the plugin main class by this anonymous function
     */
    @Suppress("UNCHECKED_CAST")
    fun <T: PluginMetadata> registerPlugin(
        pluginJarPath: String,
        metadataClazz: Class<T>,
        pluginMainClass: (RawLoadedPlugin, T) -> String = { _, metadata -> metadata.main }
    ) {
        val raw = PluginLoader.loadPlugin(pluginJarPath, metadataClazz)
        val metadata = raw.metadata
        val classLoader = raw.classLoader

        PluginLoaderHook.onPluginLoadedListeners.forEach {
            it.onLoaded(raw)
        }

        val mainClassName = pluginMainClass.invoke(raw, metadata as T)
        _registeredRawPlugins[mainClassName] = raw

        val pluginInstance = classLoader.loadClass(mainClassName).getDeclaredConstructor().newInstance() as AbstractPlugin
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

        _registeredRawPlugins.remove(mainClassName)
        _registeredPlugins[mainClassName] = loadedPlugin

        PluginLoaderHook.onPluginEnabledListeners.forEach {
            it.onEnabled(loadedPlugin)
        }
    }

    fun unregisterPlugin(plugin: AbstractPlugin) {
        _registeredPlugins[plugin.pluginClassName]?.pluginInstance?.onDisabled()
    }
}