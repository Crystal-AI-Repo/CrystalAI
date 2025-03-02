package com.lovelycatv.ai.crystal.node.plugin

import com.lovelycatv.ai.crystal.node.service.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.service.EmbeddingServiceDispatcher
import org.springframework.context.ApplicationContext

/**
 * @author lovelycat
 * @since 2025-03-02 16:56
 * @version 1.0
 */
class LoadedPlugin(
    val pluginInstance: NodePlugin,
    val embeddingServiceDispatchers: List<EmbeddingServiceDispatcher>,
    val chatServiceDispatchers: List<ChatServiceDispatcher>,
    metadata: PluginMetadata,
    context: ApplicationContext,
    classLoader: ClassLoader,
    rawPluginConfig: Map<String, Any?>
) : RawLoadedPlugin(metadata, context, classLoader, rawPluginConfig)