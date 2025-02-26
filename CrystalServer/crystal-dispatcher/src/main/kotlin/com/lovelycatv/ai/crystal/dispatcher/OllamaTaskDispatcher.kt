package com.lovelycatv.ai.crystal.dispatcher

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-02-27 00:17
 * @version 1.0
 */
@Component
class OllamaTaskDispatcher(
    private val nodeManager: AbstractNodeManager,
    private val ollamaTaskManager: OllamaTaskManager
) {
    /**
     * Retrieve the best available node based on the scheduling rules. If no nodes are available, return null.
     *
     * @return Available [RegisteredNode]
     */
    fun requireAvailableNode(): RegisteredNode? {
        return nodeManager.allRegisteredNodes.filter { it.isNettyClientConnected }.randomOrNull()
    }

    /**
     * To ensure the uniqueness of the sessionId for all messages across all nodes,
     * use this function to generate a UUID that will never be repeated.
     *
     * @return UUID as sessionId
     */
    fun requireSessionId(): String {
        var uuid = UUID.randomUUID().toString()
        while (ollamaTaskManager.getSession(uuid) != null) {
            uuid = UUID.randomUUID().toString()
        }
        return uuid
    }
}