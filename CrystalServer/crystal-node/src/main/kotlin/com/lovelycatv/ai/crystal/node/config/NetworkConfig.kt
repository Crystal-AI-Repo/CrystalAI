package com.lovelycatv.ai.crystal.node.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress

/**
 * @author lovelycat
 * @since 2025-02-06 18:29
 * @version 1.0
 */
@Configuration
class NetworkConfig {
    @Bean("localIpAddress")
    fun localIpAddress(): String {
        return InetAddress.getLocalHost().hostAddress
    }
}