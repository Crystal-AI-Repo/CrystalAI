package com.lovelycatv.ai.crystal.dispatcher.task.dispatcher

import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import com.lovelycatv.ai.crystal.dispatcher.task.AbstractTask
import com.lovelycatv.ai.crystal.dispatcher.task.TaskPerformResult
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import java.util.*

/**
 * @author lovelycat
 * @since 2025-02-28 03:35
 * @version 1.0
 */
abstract class AbstractTaskDispatcher(
    protected val nodeManager: AbstractNodeManager,
    protected val taskManager: TaskManager
) {
    abstract suspend fun performTask(task: AbstractTask): TaskPerformResult<String>?

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
        while (taskManager.getSession(uuid) != null) {
            uuid = UUID.randomUUID().toString()
        }
        return uuid
    }
}