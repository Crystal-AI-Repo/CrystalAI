package com.lovelycatv.ai.crystal.dispatcher.task.dispatcher

import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.chat.options.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.netty.sendMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import com.lovelycatv.ai.crystal.dispatcher.task.*
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-27 00:17
 * @version 1.0
 */
@Component
class TaskDispatcher(
    nodeManager: AbstractNodeManager,
    taskManager: TaskManager
) : AbstractTaskDispatcher(nodeManager, taskManager) {
    private val logger = logger()

    override suspend fun performTask(task: AbstractTask): TaskPerformResult<String> {
        val availableNodeResult = super.requireAvailableNode(task, TaskDispatchStrategy.RANDOM)

        if (!availableNodeResult.success) {
            logger.error("${availableNodeResult.message}, task: ${task.toJSONString()}")
            return TaskPerformResult.failed(
                taskId = task.taskId,
                data = "",
                message = availableNodeResult.message ?: "",
                cause = null
            )
        }

        val availableNode = availableNodeResult.node ?: throw IllegalStateException("Capable node found but acquired null")

        val taskId = task.taskId

        return if (task is AbstractChatTask<*>) {
            val sessionId = super.requireSessionId()

            logger.info("Executing ${task::class.simpleName}-[${taskId}], allocated node: [${availableNode.nodeName}], sessionId: [${sessionId}], options: [${task.options.toJSONString()}]")

            val message = MessageChainBuilder {
                // Random sessionId
                this.sessionId(sessionId)

                if (task is StreamChatTask<*>) {
                    // Enable streaming
                    this.streamId()
                } else {
                    // No streaming
                    this.streamId(null)
                }

                // Add OllamaChatOptions is non-null
                task.options?.let {
                    this.addMessage(it)
                }

                // Add all messages
                this.addMessages(task.prompts)
            }

            val result = availableNode.channel.sendMessage(message)

            if (result.success) {
                taskManager.pushSession(recipient = availableNode, messageChain = message, timeout = task.timeout)
                TaskPerformResult.success(
                    taskId = taskId,
                    data = sessionId
                )
            } else {
                logger.error("Task-[${taskId}] execution failed, reason: ${result.reason.name}", result.cause)
                TaskPerformResult.failed(
                    taskId = taskId,
                    data = "",
                    message = "Message send failed, reason: ${result.reason.name}",
                    cause = result.cause
                )
            }
        } else {
            TaskPerformResult.failed(
                taskId = taskId,
                data = "",
                message = "Task type [${task::class.qualifiedName}] is not supported currently"
            )
        }
    }

    /**
     * Check whether the node could perform this task
     *
     * @param node [RegisteredNode]
     * @param task Task to be performed
     * @return [Boolean]
     */
    override fun canNodePerform(node: RegisteredNode, task: AbstractTask): Boolean {
        return if (task is AbstractChatTask<*>) {
            when (task.options) {
                is OllamaChatOptions -> {
                    node.ollamaModels.find { it.model == task.options.modelName } != null
                }
                is DeepSeekChatOptions -> {
                    node.deepseekModels.find { it.id == task.options.modelName } != null
                }
                else -> false
            }
        } else false
    }


}