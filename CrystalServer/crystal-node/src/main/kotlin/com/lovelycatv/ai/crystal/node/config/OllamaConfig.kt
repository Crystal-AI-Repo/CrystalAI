package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.common.client.impl.OllamaClient
import com.lovelycatv.ai.crystal.common.client.getFeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-02-09 21:55
 * @version 1.0
 */
@Configuration
class OllamaConfig(
    val nodeConfiguration: NodeConfiguration
) {
    @Bean
    fun ollamaFeignClient(): OllamaClient {
        return getFeignClient<OllamaClient>(nodeConfiguration.ollama.baseUrl)
    }
}