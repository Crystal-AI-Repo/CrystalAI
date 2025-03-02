package com.lovelycatv.crystal.plugin

import com.lovelycatv.crystal.plugin.config.YAMLConfiguration
import com.lovelycatv.crystal.plugin.logger.PluginLogger
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * @author lovelycat
 * @since 2025-03-02 15:52
 * @version 1.0
 */
abstract class AbstractPlugin {
    val pluginClassName: String = this::class.qualifiedName!!

    val logger = PluginLogger(this.getPluginMetadata().name)

    open fun onLoad() {
        val it = this.getPluginMetadata()
        logger.info("Plugin ${it.name} (${it.version}) loaded, prepared to be enabled. " +
            "entryPoint: ${it.main}, basePackage: ${it.packageName}, authors: ${it.authors}")
    }

    open fun onEnabled() {}

    open fun onDisabled() {
        logger.info("Plugin is going down")
    }

    fun getPluginMetadata() = PluginManager.registeredPlugins[pluginClassName]?.metadata
        ?: PluginManager.registeredRawPlugins[pluginClassName]!!.metadata

    fun getConfig(): YAMLConfiguration = PluginManager.registeredPlugins[pluginClassName]!!.pluginConfig

    fun saveDefaultConfig() {
        val jarDir = System.getProperty("user.dir")
        val pluginConfigDir = File(File(File(jarDir), "plugins"), this.getPluginMetadata().name)

        if (!pluginConfigDir.exists()) {
            pluginConfigDir.mkdirs()
        }

        val targetFile = File(pluginConfigDir, "config.yml")

        if (!targetFile.exists()) {
            val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml")
                ?: throw IllegalArgumentException("Could not find config.yml in classpath")

            resourceStream.use { input ->
                Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            logger.info("Default configuration has been saved in ${pluginConfigDir.absolutePath}")
        } else {
            logger.info("Configuration already exists. Path: ${targetFile.absolutePath}")
        }
    }
}