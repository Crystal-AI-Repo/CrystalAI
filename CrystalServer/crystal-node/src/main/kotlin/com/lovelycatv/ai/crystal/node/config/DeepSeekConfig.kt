package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.common.client.DeepSeekClient
import com.lovelycatv.ai.crystal.common.client.getFeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-02-28 18:12
 * @version 1.0
 */
@Configuration
class DeepSeekConfig(
    val nodeConfiguration: NodeConfiguration
) {
    @Bean
    fun deepseekFeignClient(): DeepSeekClient {
        return getFeignClient<DeepSeekClient>(nodeConfiguration.deepseek.baseUrl)
    }
}