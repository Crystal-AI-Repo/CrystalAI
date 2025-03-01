package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.node.aop.TaskQueueAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * @author lovelycat
 * @since 2025-03-01 23:01
 * @version 1.0
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false)
class AspectConfig {
    @Bean
    fun taskQueueAspect(): TaskQueueAspect {
        return TaskQueueAspect()
    }
}