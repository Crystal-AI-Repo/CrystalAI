package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.node.interceptor.AuthorizationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import java.nio.charset.StandardCharsets

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

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        /**
         * Fix: default content type encoded with ISO-8859-1,
         *      fixed to *;charset=UTF-8
         */
        for (converter in converters) {
            if (converter is StringHttpMessageConverter) {
                converter.defaultCharset = StandardCharsets.UTF_8
            }
            if (converter is MappingJackson2HttpMessageConverter) {
                converter.defaultCharset = StandardCharsets.UTF_8
            }
        }
    }
}