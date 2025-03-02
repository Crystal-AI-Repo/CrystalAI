package com.lovelycatv.ai.crystal.node.plugin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.lovelycatv.ai.crystal.node.plugin.exception.PluginMetadataNotFoundException
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
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
class PluginLoader {
    companion object {
        private val yamlMapper = ObjectMapper(YAMLFactory()).apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        fun getPluginMetadata(pluginJarPath: String): PluginMetadata {
            JarFile(pluginJarPath).use { jarFile ->
                val entry: JarEntry = jarFile.getJarEntry("plugin.yml") ?: throw PluginMetadataNotFoundException(jarFile.name)
                jarFile.getInputStream(entry).use { inputStream ->
                    return yamlMapper.readValue(inputStream, PluginMetadata::class.java)
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun getPluginConfig(pluginJarPath: String): Map<String, Any?> {
            JarFile(pluginJarPath).use { jarFile ->
                val entry: JarEntry = jarFile.getJarEntry("plugin.yml") ?: throw PluginMetadataNotFoundException(jarFile.name)
                return yamlMapper.readValue(jarFile.getInputStream(entry), Map::class.java) as Map<String, Any?>
            }
        }
    }

    @Throws(Exception::class)
    fun loadPlugin(pluginJarPath: String): RawLoadedPlugin {
         val pluginMetadata = getPluginMetadata(pluginJarPath)

        val jarUrl: URL = File(pluginJarPath).toURI().toURL()
        val pluginClassLoader = URLClassLoader(arrayOf(jarUrl), javaClass.classLoader)

        val pluginContext = AnnotationConfigApplicationContext()
        pluginContext.classLoader = pluginClassLoader
        pluginContext.scan(pluginMetadata.packageName)
        pluginContext.refresh()

        return RawLoadedPlugin(
            metadata = pluginMetadata,
            context = pluginContext,
            classLoader = pluginClassLoader,
            rawPluginConfig = getPluginConfig(pluginJarPath)
        )
    }
}