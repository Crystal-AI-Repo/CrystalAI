package com.lovelycatv.crystal.plugin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.lovelycatv.crystal.plugin.config.YAMLConfiguration
import com.lovelycatv.crystal.plugin.data.PluginMetadata
import com.lovelycatv.crystal.plugin.data.RawLoadedPlugin
import com.lovelycatv.crystal.plugin.exception.PluginMetadataNotFoundException
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile


/**
 * @author lovelycat
 * @since 2025-03-02 14:54
 * @version 1.0
 */
object PluginLoader {
    val yamlMapper = ObjectMapper(YAMLFactory()).apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Throws(PluginMetadataNotFoundException::class)
    fun <T: PluginMetadata> getPluginMetadata(pluginJarPath: String, clazz: Class<T>): T {
        JarFile(pluginJarPath).use { jarFile ->
            val entry: JarEntry = jarFile.getJarEntry("plugin.yml") ?: throw PluginMetadataNotFoundException(jarFile.name)
            jarFile.getInputStream(entry).use { inputStream ->
                return yamlMapper.readValue(inputStream, clazz)
            }
        }
    }

    @Throws(Exception::class)
    fun <T: PluginMetadata> loadPlugin(pluginJarPath: String, clazz: Class<T>): RawLoadedPlugin {
        val pluginMetadata = getPluginMetadata(pluginJarPath, clazz)

        val jarUrl: URL = File(pluginJarPath).toURI().toURL()
        val pluginClassLoader = URLClassLoader(arrayOf(jarUrl), javaClass.classLoader)

        return RawLoadedPlugin(
            metadata = pluginMetadata,
            classLoader = pluginClassLoader
        )
    }

    fun getPluginConfig(pluginConfigPath: String): YAMLConfiguration {
        return YAMLConfiguration(File(pluginConfigPath))
    }
}