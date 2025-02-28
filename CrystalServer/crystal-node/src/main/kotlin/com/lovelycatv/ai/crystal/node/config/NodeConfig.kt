package com.lovelycatv.ai.crystal.node.config

import com.lovelycatv.ai.crystal.node.queue.DefaultChatTaskQueue
import com.lovelycatv.ai.crystal.node.queue.InMemoryChatTaskQueue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-02-28 18:13
 * @version 1.0
 */
@Configuration
class NodeConfig {
    @Bean
    fun taskQueue(): DefaultChatTaskQueue {
        return InMemoryChatTaskQueue()
    }
}