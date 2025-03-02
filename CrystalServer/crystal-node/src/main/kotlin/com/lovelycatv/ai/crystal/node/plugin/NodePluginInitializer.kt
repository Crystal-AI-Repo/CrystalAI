package com.lovelycatv.ai.crystal.node.plugin

import com.lovelycatv.ai.crystal.common.util.logger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.io.File

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
        val pluginsDir = File(File("").absolutePath, "plugins")
        logger.info("Loading plugins in ${pluginsDir.absolutePath}")
        pluginsDir.listFiles()?.filter { it.isFile }?.forEach {
            logger.info("Plugin: ${it.nameWithoutExtension} detected")
            PluginManager.registerPlugin(it.absolutePath)
        }
    }
}