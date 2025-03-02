package com.lovelycatv.ai.crystal.dispatcher.task.dispatcher

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.common.netty.NettyMessageSendResult
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

    /**
     * Request to perform a task.
     * The data of the returned [TaskPerformResult] is sessionId (If successful)
     *
     * @param task [AbstractTask] to be performed
     * @return [TaskPerformResult], inner data is Pair<SessionId, StreamId?>
     */
    override suspend fun performTask(task: AbstractTask): TaskPerformResult<Pair<String, String?>> {
        val availableNodeResult = super.requireAvailableNode(task, TaskDispatchStrategy.RANDOM)

        if (!availableNodeResult.success) {
            logger.error("${availableNodeResult.message}, task: ${task.toJSONString()}")
            return TaskPerformResult.failed(
                taskId = task.taskId,
                data = "" to null,
                message = availableNodeResult.message ?: "",
                cause = null
            )
        }

        val availableNode = availableNodeResult.node ?: throw IllegalStateException("Capable node found but acquired null")

        val taskId = task.taskId
        val sessionId = super.requireSessionId()

        return when (task) {
            is AbstractModelTask<*> -> {
                logger.info("Executing ${task::class.simpleName}-[${taskId}], " +
                    "allocated node: [${availableNode.nodeName}], " +
                    "sessionId: [${sessionId}], options: [${task.options.toJSONString()}]"
                )

                val message = MessageChainBuilder {
                    // Random sessionId
                    this.sessionId(sessionId)

                    if (task is AbstractChatTask<*>) {
                        if (task is StreamChatTask<*>) {
                            // Enable streaming
                            this.streamId()
                        } else {
                            // No streaming
                            this.streamId(null)
                        }
                    } else if (task is AbstractEmbeddingTask<*>) {
                        // No streaming
                        this.streamId(null)
                    }

                    task.options.let {
                        this.addMessage(it)
                    }

                    // Add all messages
                    this.addMessages(task.prompts)
                }

                val result = availableNode.channel.sendMessage(message)

                processNettySendMessageResult(availableNode, message, task, result)
            }
            else -> {
                logger.error("Task type [${task::class.qualifiedName}] is not supported currently")

                TaskPerformResult.failed(
                    taskId = taskId,
                    data = "" to null,
                    message = "Task type [${task::class.qualifiedName}] is not supported currently"
                )
            }
        }
    }

    private fun processNettySendMessageResult(
        availableNode: RegisteredNode,
        message: MessageChain,
        task: AbstractTask,
        result: NettyMessageSendResult
    ): TaskPerformResult<Pair<String, String?>> {
        return if (result.success) {
            taskManager.pushSession(recipient = availableNode, messageChain = message, timeout = task.timeout)
            TaskPerformResult.success(
                taskId = task.taskId,
                data = message.sessionId to message.streamId
            )
        } else {
            logger.error("Task-[${task.taskId}] execution failed, reason: ${result.reason.name}", result.cause)

            TaskPerformResult.failed(
                taskId = task.taskId,
                data = "" to null,
                message = "Message send failed, reason: ${result.reason.name}",
                cause = result.cause
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
        return if (task is AbstractModelTask<*>) {
            when (task) {
                is AbstractChatTask<*> -> {
                    when (task.options) {
                        is OllamaChatOptions -> {
                            node.ollamaModels.find { it.model == task.options.modelName } != null
                        }

                        is DeepSeekChatOptions -> {
                            node.deepseekModels.find { it.id == task.options.modelName } != null
                        }

                        else -> false
                    }
                }

                is AbstractEmbeddingTask<*> -> {
                    when (task.options) {
                        is OllamaEmbeddingOptions -> {
                            node.ollamaModels.find { it.model == task.options.modelName } != null
                        }

                        else -> false
                    }
                }

                else -> false
            }
        } else false
    }


}