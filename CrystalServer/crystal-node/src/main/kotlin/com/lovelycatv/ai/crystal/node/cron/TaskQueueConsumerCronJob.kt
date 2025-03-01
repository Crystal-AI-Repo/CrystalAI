package com.lovelycatv.ai.crystal.node.cron

import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.copyAndAddMessages
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.data.AbstractTask
import com.lovelycatv.ai.crystal.node.data.ChatTask
import com.lovelycatv.ai.crystal.node.data.EmbeddingTask
import com.lovelycatv.ai.crystal.node.exception.UnsupportedTaskTypeException
import com.lovelycatv.ai.crystal.node.netty.AbstractNodeNettyClient
import com.lovelycatv.ai.crystal.node.queue.InMemoryTaskQueue
import com.lovelycatv.ai.crystal.node.service.chat.DeepSeekChatService
import com.lovelycatv.ai.crystal.node.service.chat.OllamaChatService
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCompletedCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamRequestFailedCallback
import com.lovelycatv.ai.crystal.node.service.embedding.OllamaEmbeddingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lovelycat
 * @since 2025-02-26 22:08
 * @version 1.0
 */
@Component
@EnableScheduling
class TaskQueueConsumerCronJob(
    private val chatTaskQueue: InMemoryTaskQueue<AbstractTask>,
    private val nodeNettyClient: AbstractNodeNettyClient,
    private val ollamaChatService: OllamaChatService,
    private val deepSeekChatService: DeepSeekChatService,
    private val ollamaEmbeddingService: OllamaEmbeddingService
) {
    private val logger = logger()

    private val taskPerformanceJob = Job()
    private val taskPerformanceCoroutineScope = CoroutineScope(Dispatchers.IO + taskPerformanceJob)

    /**
     * As the message might be sent simultaneously, leading to incorrect data, the lock must be acquired when the message is being sent.
     */
    private val lock = Mutex()

    @Scheduled(cron = "0/1 * * * * ?")
    fun consume() {
        val task = chatTaskQueue.requireTask()

        if (task != null) {
            when (task) {
                is ChatTask<*> -> {
                    this.performChatTask(task)
                }
                is EmbeddingTask<*> -> {
                    this.performEmbeddingTask(task)
                }
                else -> {
                    throw UnsupportedTaskTypeException(task::class.qualifiedName)
                }
            }
        }
    }

    private fun performEmbeddingTask(task: EmbeddingTask<*>) {
        task.performInWrapper(
            doSuspend = { sessionId, messageTemplate, unlock ->
                // Blocking response
                val result = when (task.type) {
                    ChatTask.Type.OLLAMA -> {
                        ollamaEmbeddingService.embedding(task.chatOptions as OllamaEmbeddingOptions?, task.prompts)
                    }
                    else -> {
                        throw UnsupportedTaskTypeException("${task.type} in EmbeddingTask is not supported")
                    }
                }

                unlock.invoke()

                messageTemplate.safeSendAsynchronously(
                    EmbeddingResponseMessage.success(
                        GlobalConstants.Flags.MESSAGE_FINISHED,
                        results = result.results.map { it.output.map { it.toDouble() }.toDoubleArray() }
                    )
                )
            },
            doStream = { sessionId, messageTemplate, unlock ->
                messageTemplate.safeSendAsynchronously(
                    EmbeddingResponseMessage(success = false, message = "Stream request for embedding is not supported")
                )
            }
        )
    }

    private fun performChatTask(task: ChatTask<*>) {
        task.performInWrapper(
            doSuspend = { sessionId, messageTemplate, unlock ->
                // Blocking response
                val result = when (task.type) {
                    ChatTask.Type.OLLAMA -> {
                        ollamaChatService.blockingGenerate(task.prompts, task.chatOptions as OllamaChatOptions?)
                    }
                    ChatTask.Type.DEEPSEEK -> {
                        deepSeekChatService.blockingGenerate(task.prompts, task.chatOptions as DeepSeekChatOptions?)
                    }
                }

                unlock.invoke()

                messageTemplate.safeSendAsynchronously(
                    if (result.success) {
                        val response = result.data!!
                        ChatResponseMessage(
                            success = true,
                            message = GlobalConstants.Flags.MESSAGE_FINISHED,
                            content = response.result.output.content,
                            generatedTokens = response.metadata.usage.generationTokens,
                            totalTokens = response.metadata.usage.totalTokens
                        )
                    } else {
                        ChatResponseMessage.failed(result.message ?: "")
                    }
                )
            },
            doStream = { sessionId, messageTemplate, unlock ->
                // Streaming response
                val messageCounter = AtomicLong(-1L)

                val onNewTokenReceived: ChatStreamCallback = {
                    messageTemplate.safeSendAsynchronously(
                        ChatResponseMessage(
                            success = true,
                            message = messageCounter.incrementAndGet().toString(),
                            content = it,
                            generatedTokens = 0,
                            totalTokens = 0
                        )
                    )
                }

                val onCompleted: ChatStreamCompletedCallback = { _, generatedTokens, totalTokens ->
                    unlock.invoke()

                    messageTemplate.safeSendAsynchronously(
                        ChatResponseMessage(
                            success = true,
                            message = GlobalConstants.Flags.STREAMING_MESSAGE_FINISHED,
                            content = null,
                            generatedTokens = generatedTokens,
                            totalTokens = totalTokens
                        )
                    )
                }

                val onFailed: ChatStreamRequestFailedCallback = {
                    Global.unlockTaskRunningStatus(sessionId)

                    messageTemplate.safeSendAsynchronously(
                        ChatResponseMessage.failed(it.message)
                    )
                }

                when (task.type) {
                    ChatTask.Type.OLLAMA -> {
                        ollamaChatService.streamGenerate(task.prompts, task.chatOptions as OllamaChatOptions?, onNewTokenReceived, onFailed, onCompleted)
                    }
                    ChatTask.Type.DEEPSEEK -> {
                        deepSeekChatService.streamGenerate(task.prompts, task.chatOptions as DeepSeekChatOptions?, onNewTokenReceived, onFailed, onCompleted)
                    }
                }
            }
        )
    }

    private fun MessageChain.safeSendAsynchronously(vararg messages: AbstractMessage) {
        this@TaskQueueConsumerCronJob.safeSendMessageAsynchronously(this.copyAndAddMessages(*messages))
    }

    private fun safeSendMessageAsynchronously(messageChain: MessageChain) {
        taskPerformanceCoroutineScope.launch {
            lock.withLock {
                nodeNettyClient.sendMessage(messageChain)
            }
        }
    }

    private suspend fun safeSendMessage(messageChain: MessageChain) {
        lock.withLock {
            nodeNettyClient.sendMessage(messageChain)
        }
    }

    private fun <T: AbstractTask> performTaskWrapper(
        task: T,
        doSuspend: TaskPerformFunction,
        doStream: TaskPerformFunction
    ) {
        val sessionId = task.requesterSessionId

        logger.info("${task.taskType.name}-Task consumed, sessionId: ${sessionId}, maxExecTime: ${task.expireTime}ms")

        val messageTemplate = MessageChainBuilder {
            // Copy the sessionId
            this.sessionId(sessionId)

            if (task.originalMessageChain.isStream()) {
                this.streamId(task.requesterStreamId)
            } else {
                this.streamId(null)
            }
        }

        // Run in coroutine scope
        taskPerformanceCoroutineScope.launch {
            if (task.originalMessageChain.isStream()) {
                doStream.invoke(sessionId, messageTemplate) {
                    Global.unlockTaskRunningStatus(sessionId)
                }
            } else {
                doSuspend.invoke(sessionId, messageTemplate) {
                    Global.unlockTaskRunningStatus(sessionId)
                }
            }
        }
    }

    private fun <T: AbstractTask> T.performInWrapper(
        doSuspend: TaskPerformFunction,
        doStream: TaskPerformFunction
    ) {
        this@TaskQueueConsumerCronJob.performTaskWrapper(this, doSuspend, doStream)
    }
}

typealias TaskPerformFunction = suspend (sessionId: String, messageTemplate: MessageChain, unlock: () -> Unit) -> Unit