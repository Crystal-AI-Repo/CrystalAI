package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.common.client.getFeignClient
import com.lovelycatv.ai.crystal.node.client.NodeDispatcherClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-02-06 17:58
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "crystal")
data class NodeConfiguration(
    var dispatcher: NodeDispatcherConfiguration = NodeDispatcherConfiguration(),
    var ollama: NodeOllamaConfiguration = NodeOllamaConfiguration()
) {
    @Bean
    fun nodeDispatcherClient(): NodeDispatcherClient {
        return getFeignClient<NodeDispatcherClient>(dispatcher.getBaseUrl(false))
    }
}

