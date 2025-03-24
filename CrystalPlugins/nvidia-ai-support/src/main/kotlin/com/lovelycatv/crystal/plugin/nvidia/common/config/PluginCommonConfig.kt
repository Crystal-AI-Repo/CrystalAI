package com.lovelycatv.crystal.plugin.nvidia.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessageTypeRegistry
import com.lovelycatv.crystal.plugin.nvidia.common.message.NvidiaChatOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-03-02 16:29
 * @version 1.0
 */
@Configuration
@ComponentScan("com.lovelycatv.crystal.plugin.nvidia.common")
open class PluginCommonConfig {
    @Bean
    open fun messageTypesRegistry(): AbstractMessageTypeRegistry {
        return object : AbstractMessageTypeRegistry {
            override fun registerTypes(objectMapper: ObjectMapper) {
                objectMapper.registerSubtypes(NvidiaChatOptions::class.java)
                objectMapper.addMixIn(AbstractMessage::class.java, AbstractMessageMixIn::class.java)
            }
        }
    }
}