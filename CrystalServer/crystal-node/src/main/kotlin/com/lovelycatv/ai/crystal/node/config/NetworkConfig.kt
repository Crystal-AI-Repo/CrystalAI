package com.lovelycatv.ai.crystal.node.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
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
    @Value("\${server.port}")
    var applicationPort: Int = 8080

    @Bean("localIpAddress")
    fun localIpAddress(): String {
        return InetAddress.getLocalHost().hostAddress
    }
}