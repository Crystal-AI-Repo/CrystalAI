package com.lovelycatv.ai.crystal.dispatcher.task.dispatcher

import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import com.lovelycatv.ai.crystal.dispatcher.response.AvailableNodeRequireResult
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
    /**
     * Request to perform a task.
     * The data of the returned [TaskPerformResult] is sessionId (If successful)
     *
     * @param task [AbstractTask] to be performed
     * @return [TaskPerformResult], inner data is Pair<SessionId, StreamId?>
     */
    abstract suspend fun performTask(task: AbstractTask): TaskPerformResult<Pair<String, String?>>

    /**
     * Check whether the node could perform this task
     *
     * @param node [RegisteredNode]
     * @param task Task to be performed
     * @return [Boolean]
     */
    protected abstract fun canNodePerform(node: RegisteredNode, task: AbstractTask): Boolean

    /**
     * Retrieve the best available node based on the scheduling rules. If no nodes are available, return null.
     *
     * @param task Task to be performed
     * @param strategy When dispatcher could not found an available node,
     *                 then the strategy will be applied.
     * @return Available [RegisteredNode]
     */
    fun requireAvailableNode(task: AbstractTask, strategy: TaskDispatchStrategy): AvailableNodeRequireResult {
        val workingNodeIds = taskManager.currentSessions.map { it.nodeId }

        val nettyConnectedNodes = nodeManager.allRegisteredNodes.filter { it.isNettyClientConnected }
        if (nettyConnectedNodes.isEmpty()) {
            return AvailableNodeRequireResult.failed("No available nodes")
        }

        val allFitNodes = nettyConnectedNodes.filter { this.canNodePerform(it, task) }
        if (allFitNodes.isEmpty()) {
            return AvailableNodeRequireResult.failed("Non of nodes could perform this task")
        }

        val idleNode = allFitNodes.filter {
            it.nodeId !in workingNodeIds
        }.randomOrNull()

        return if (idleNode != null) {
            AvailableNodeRequireResult.success(idleNode)
        } else {
            when (strategy) {
                TaskDispatchStrategy.REJECT -> {
                    AvailableNodeRequireResult.failed("No capable node found, task rejected by strategy")
                }
                TaskDispatchStrategy.RANDOM -> {
                    AvailableNodeRequireResult.success(allFitNodes.random())
                }
            }
        }

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