package com.lovelycatv.ai.crystal.node.plugin

import com.lovelycatv.ai.crystal.common.data.message.AbstractMessageTypeRegistry
import com.lovelycatv.ai.crystal.common.plugin.CrystalPluginMetadata
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.api.dispatcher.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.api.dispatcher.EmbeddingServiceDispatcher
import com.lovelycatv.ai.crystal.node.api.task.NodeChatTaskBuilder
import com.lovelycatv.ai.crystal.node.api.task.NodeEmbeddingTaskBuilder
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
class NodePluginInitializer : CommandLineRunner {
    private val logger = logger()

    /**
     * Callback used to run the bean.
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    override fun run(vararg args: String?) {
        val pluginsDir = NodePluginManager.pluginsDir

        logger.info("Loading plugins in ${pluginsDir.absolutePath}")

        PluginLoaderHook.addOnPluginEnabledListener { loadedPlugin ->
            val pluginContext = AnnotationConfigApplicationContext()
            pluginContext.classLoader = loadedPlugin.classLoader
            pluginContext.scan(
                (loadedPlugin.metadata as CrystalPluginMetadata).commonPackageName,
                (loadedPlugin.metadata as CrystalPluginMetadata).nodePackageName
            )
            pluginContext.refresh()

            val nodePlugin = loadedPlugin.pluginInstance as AbstractNodePlugin
            nodePlugin.setPluginContext(pluginContext)

            nodePlugin.chatServiceDispatchers.addAll(nodePlugin.getBeans(ChatServiceDispatcher::class))
            nodePlugin.embeddingServiceDispatchers.addAll(nodePlugin.getBeans(EmbeddingServiceDispatcher::class))

            nodePlugin.chatTaskBuilders.addAll(nodePlugin.getBeans(NodeChatTaskBuilder::class))
            nodePlugin.embeddingTaskBuilders.addAll(nodePlugin.getBeans(NodeEmbeddingTaskBuilder::class))

            NodePluginManager.addRegisteredPlugin(nodePlugin)

            // Register message types to the Global ObjectMapper
            val messageTypeRegistries = pluginContext.getBeansOfType(AbstractMessageTypeRegistry::class.java).values.filterNotNull()
            messageTypeRegistries.forEach {
                it.registerTypes(NodePluginManager.objectMapper)
            }

            logger.info("==================================================================================================")
            logger.info("Plugin ${loadedPlugin.metadata.name} (${loadedPlugin.metadata.version}) is enabled successfully.")
            logger.info("${nodePlugin.getBeans(Any::class).size} beans detected in total")
            logger.info("")
            logger.info("MessageTypes: ${messageTypeRegistries.size}")
            messageTypeRegistries.forEachIndexed { index, it ->
                logger.info("> ${index + 1}. ${it::class.java.typeName}")
            }
            logger.info("")
            logger.info("ChatServiceDispatchers: ${nodePlugin.chatServiceDispatchers.size}")
            nodePlugin.chatServiceDispatchers.forEachIndexed { index, it ->
                logger.info("> ${index + 1}. ${it::class.java.typeName}")
            }
            logger.info("")
            logger.info("EmbeddingServiceDispatchers: ${nodePlugin.embeddingServiceDispatchers.size}")
            nodePlugin.embeddingServiceDispatchers.forEachIndexed { index, it ->
                logger.info("> ${index + 1}. ${it::class.java.typeName}")
            }
            logger.info("")
            logger.info("ChatTaskBuilders: ${nodePlugin.chatTaskBuilders.size}")
            nodePlugin.chatTaskBuilders.forEachIndexed { index, it ->
                logger.info("> ${index + 1}. ${it::class.java.typeName}")
            }
            logger.info("")
            logger.info("EmbeddingTaskBuilders: ${nodePlugin.embeddingTaskBuilders.size}")
            nodePlugin.embeddingTaskBuilders.forEachIndexed { index, it ->
                logger.info("> ${index + 1}. ${it::class.java.typeName}")
            }
            logger.info("==================================================================================================")
        }

        pluginsDir.listFiles()?.filter { it.isFile && it.extension == "jar" }?.forEach {
            PluginManager.registerPlugin(
                pluginJarPath = it.absolutePath,
                metadataClazz = CrystalPluginMetadata::class.java,
                pluginMainClass = { _, metadata -> metadata.nodeMain }
            )
        }
    }
}