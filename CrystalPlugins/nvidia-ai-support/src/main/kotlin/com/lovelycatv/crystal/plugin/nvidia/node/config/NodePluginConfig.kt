package com.lovelycatv.crystal.plugin.nvidia.node.config

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.node.api.dispatcher.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.api.task.NodeChatTaskBuilder
import com.lovelycatv.ai.crystal.node.data.AbstractChatResult
import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractChatService
import com.lovelycatv.ai.crystal.node.task.ChatTask
import com.lovelycatv.crystal.plugin.nvidia.common.message.NvidiaChatOptions
import com.lovelycatv.crystal.plugin.nvidia.node.service.NvidiaPlatformChatService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-03-23 20:59
 * @version 1.0
 */
@Suppress("UNCHECKED_CAST")
@Configuration
@ComponentScan("com.lovelycatv.crystal.plugin.nvidia.node")
open class NodePluginConfig(
    private val nvidiaPlatformChatService: NvidiaPlatformChatService
) {
    @Bean
    open fun chatServiceDispatcher(): ChatServiceDispatcher {
        return ChatServiceDispatcher { options ->
            when (options) {
                NvidiaChatOptions::class -> nvidiaPlatformChatService
                else -> null
            } as? AbstractChatService<AbstractChatOptions, AbstractChatResult, Any>?
        }
    }

    @Bean
    open fun nvidiaChatTaskBuilder(): NodeChatTaskBuilder<NvidiaChatOptions> {
        return NodeChatTaskBuilder {
            ChatTask(it, 0, 0, NvidiaChatOptions::class)
        }
    }
}