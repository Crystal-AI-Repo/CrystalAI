package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.node.interceptor.AuthorizationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

/**
 * @author lovelycat
 * @since 2025-03-28 16:57
 * @version 1.0
 */
@Configuration
class InterceptorConfig(
    private val nodeConfiguration: NodeConfiguration
) : WebMvcConfigurationSupport() {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AuthorizationInterceptor(nodeConfiguration.secretKey))
            .addPathPatterns("/**")
            .excludePathPatterns("/api/*/auth/**")
            .excludePathPatterns("/api/*/probe/**")
        super.addInterceptors(registry)
    }
}