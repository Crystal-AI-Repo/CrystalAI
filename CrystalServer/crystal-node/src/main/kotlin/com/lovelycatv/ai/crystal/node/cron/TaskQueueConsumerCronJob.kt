package com.lovelycatv.ai.crystal.node.cron

import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.copyAndAddMessages
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.api.dispatcher.ChatServiceDispatcher
import com.lovelycatv.ai.crystal.node.api.dispatcher.EmbeddingServiceDispatcher
import com.lovelycatv.ai.crystal.node.data.AbstractChatResult
import com.lovelycatv.ai.crystal.node.data.AbstractEmbeddingResult
import com.lovelycatv.ai.crystal.node.exception.UnsupportedModelOptionsType
import com.lovelycatv.ai.crystal.node.exception.UnsupportedTaskTypeException
import com.lovelycatv.ai.crystal.node.netty.AbstractNodeNettyClient
import com.lovelycatv.ai.crystal.node.plugin.NodePluginManager
import com.lovelycatv.ai.crystal.node.queue.InMemoryTaskQueue
import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractChatService
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCompletedCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamRequestFailedCallback
import com.lovelycatv.ai.crystal.node.service.embedding.base.AbstractEmbeddingService
import com.lovelycatv.ai.crystal.node.task.AbstractTask
import com.lovelycatv.ai.crystal.node.task.ChatTask
import com.lovelycatv.ai.crystal.node.task.EmbeddingTask
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
    private val chatServiceDispatchers: List<ChatServiceDispatcher>,
    private val embeddingServiceDispatchers: List<EmbeddingServiceDispatcher>
) {
    private val logger = logger()

    private val taskPerformanceJob = Job()
    private val taskPerformanceCoroutineScope = CoroutineScope(Dispatchers.IO + taskPerformanceJob)

    private fun determineEmbeddingService(task: EmbeddingTask<*>) = null.run {
        var service: AbstractEmbeddingService<AbstractEmbeddingOptions, AbstractEmbeddingResult>? = null
        (embeddingServiceDispatchers + NodePluginManager.registeredPlugins.flatMap { it.embeddingServiceDispatchers }).forEach {
            if (service == null) {
                service = it.getService(task.embeddingOptionsClazz)
            } else {
                return@forEach
            }
        }
        service
    }

    private fun determineChatService(task: ChatTask<*>) = null.run {
        var service: AbstractChatService<AbstractChatOptions, AbstractChatResult, Any>? = null
        (chatServiceDispatchers + NodePluginManager.registeredPlugins.flatMap { it.chatServiceDispatchers }).forEach {
            if (service == null) {
                service = it.getService(task.chatOptionsClazz)
            } else {
                return@forEach
            }
        }
        service
    }

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
            doSuspend = { _, messageTemplate, unlock ->
                // Blocking response
                val service = determineEmbeddingService(task)

                val result = service?.embedding(task.chatOptions, task.prompts)
                    ?: throw UnsupportedModelOptionsType(task.embeddingOptionsClazz.qualifiedName)

                unlock.invoke()

                messageTemplate.safeSendAsynchronously(
                    EmbeddingResponseMessage.success(
                        GlobalConstants.Flags.MESSAGE_FINISHED,
                        results = result.results,
                        promptTokens = result.metadata.promptTokens,
                        totalTokens = result.metadata.totalTokens
                    )
                )
            },
            doStream = { _, messageTemplate, _ ->
                messageTemplate.safeSendAsynchronously(
                    EmbeddingResponseMessage(success = false, message = "Stream request for embedding is not supported")
                )
            }
        )
    }

    private fun performChatTask(task: ChatTask<*>) {
        task.performInWrapper(
            doSuspend = { _, messageTemplate, unlock ->
                // Blocking response
                val service = determineChatService(task)

                val result = service?.blockingGenerate(task.prompts, task.chatOptions)
                    ?: throw UnsupportedModelOptionsType(task.chatOptionsClazz.qualifiedName)

                unlock.invoke()

                messageTemplate.safeSendAsynchronously(
                    if (result.success) {
                        val response = result.data!!
                        ChatResponseMessage(
                            success = true,
                            message = GlobalConstants.Flags.MESSAGE_FINISHED,
                            content = response.results.firstOrNull()?.content,
                            generatedTokens = response.metadata.generatedTokens,
                            totalTokens = response.metadata.totalTokens
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

                val service = determineChatService(task)
                service?.streamGenerate(task.prompts, task.chatOptions, onNewTokenReceived, onFailed, onCompleted)
                    ?: throw UnsupportedModelOptionsType(task.chatOptionsClazz.qualifiedName)
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