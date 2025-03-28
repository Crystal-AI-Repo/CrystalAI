package com.lovelycatv.ai.crystal.dispatcher.plugin

import com.lovelycatv.ai.crystal.common.data.message.AbstractMessageTypeRegistry
import com.lovelycatv.ai.crystal.common.plugin.CrystalPluginMetadata
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.crystal.openapi.plugin.ChatOptionsBuilder
import com.lovelycatv.crystal.openapi.plugin.EmbeddingOptionsBuilder
import com.lovelycatv.crystal.plugin.PluginManager
import com.lovelycatv.crystal.plugin.api.PluginLoaderHook
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-03-02 14:59
 * @version 1.0
 */
@Component
class DispatcherPluginInitializer : CommandLineRunner {
    private val logger = logger()

    /**
     * Callback used to run the bean.
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    override fun run(vararg args: String?) {
        val pluginsDir = DispatcherPluginManager.pluginsDir

        logger.info("Loading plugins in ${pluginsDir.absolutePath}")

        PluginLoaderHook.addOnPluginEnabledListener { loadedPlugin ->
            val pluginContext = AnnotationConfigApplicationContext()
            pluginContext.classLoader = loadedPlugin.classLoader
            pluginContext.scan(
                (loadedPlugin.metadata as CrystalPluginMetadata).commonPackageName,
                loadedPlugin.metadata.packageName
            )
            pluginContext.refresh()

            val nodePlugin = loadedPlugin.pluginInstance as AbstractDispatcherPlugin
            nodePlugin.setPluginContext(pluginContext)

            nodePlugin.chatOptionsBuilders.addAll(nodePlugin.getBeans(ChatOptionsBuilder::class))
            nodePlugin.embeddingOptionsBuilders.addAll(nodePlugin.getBeans(EmbeddingOptionsBuilder::class))

            DispatcherPluginManager.addRegisteredPlugin(nodePlugin)

            // Register message types to the Global ObjectMapper
            val messageTypeRegistries = pluginContext.getBeansOfType(AbstractMessageTypeRegistry::class.java)
            messageTypeRegistries.values.filterNotNull().forEach {
                it.registerTypes(DispatcherPluginManager.objectMapper)
            }

            logger.info("==================================================================================================")
            logger.info("Plugin ${loadedPlugin.metadata.name} (${loadedPlugin.metadata.version}) is enabled successfully.")
            logger.info("${nodePlugin.getBeans(Any::class).size} beans detected in total")
            logger.info("")
            logger.info("ChatOptionsBuilders: ${nodePlugin.chatOptionsBuilders.size}")
            nodePlugin.chatOptionsBuilders.forEachIndexed { index, it ->
                logger.info("> ${index + 1}. ${it::class.java.typeName}")
            }
            logger.info("==================================================================================================")
        }

        pluginsDir.listFiles()?.filter { it.isFile && it.extension == "jar" }?.forEach {
            PluginManager.registerPlugin(it.absolutePath, CrystalPluginMetadata::class.java)
        }
    }
}