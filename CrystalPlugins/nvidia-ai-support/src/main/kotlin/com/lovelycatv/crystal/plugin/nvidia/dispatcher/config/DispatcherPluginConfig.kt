package com.lovelycatv.crystal.plugin.nvidia.dispatcher.config

import com.lovelycatv.crystal.openapi.plugin.ChatOptionsBuilder
import com.lovelycatv.crystal.plugin.nvidia.common.message.NvidiaChatOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-03-23 20:59
 * @version 1.0
 */
@Configuration
open class DispatcherPluginConfig {
    @Bean
    open fun nvidiaChatOptionsBuilders(): ChatOptionsBuilder<*> {
        return ChatOptionsBuilder<NvidiaChatOptions>("nvidia") {
            NvidiaChatOptions(modelName = it)
        }
    }
}