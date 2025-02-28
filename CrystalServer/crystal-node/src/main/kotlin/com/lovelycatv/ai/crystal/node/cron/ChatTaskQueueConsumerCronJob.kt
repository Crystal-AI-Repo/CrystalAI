package com.lovelycatv.ai.crystal.node.cron

import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.chat.options.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.data.ChatTask
import com.lovelycatv.ai.crystal.node.netty.AbstractNodeNettyClient
import com.lovelycatv.ai.crystal.node.queue.ChatTaskQueue
import com.lovelycatv.ai.crystal.node.service.chat.DeepSeekChatService
import com.lovelycatv.ai.crystal.node.service.chat.OllamaChatService
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCompletedCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamRequestFailedCallback
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
class ChatTaskQueueConsumerCronJob(
    private val chatTaskQueue: ChatTaskQueue<AbstractChatOptions>,
    private val nodeNettyClient: AbstractNodeNettyClient,
    private val ollamaChatService: OllamaChatService,
    private val deepSeekChatService: DeepSeekChatService
) {
    private val logger = logger()

    private val blockingRequestJob = Job()
    private val blockingRequester = CoroutineScope(Dispatchers.IO + blockingRequestJob)

    /**
     * As the message might be sent simultaneously, leading to incorrect data, the lock must be acquired when the message is being sent.
     */
    private val lock = Mutex()

    @Scheduled(cron = "0/1 * * * * ?")
    fun consume() {
        val task = chatTaskQueue.requireTask()

        if (task != null) {
            val sessionId = task.requesterSessionId

            logger.info("ChatTask-[${task.type}] consumed, sessionId: ${sessionId}, maxExecTime: ${task.expireTime}ms")

            val messageTemplate = MessageChainBuilder {
                // Copy the sessionId
                this.sessionId(sessionId)

                if (task.originalMessageChain.isStream()) {
                    this.streamId(task.requesterStreamId)
                } else {
                    this.streamId(null)
                }
            }

            if (task.originalMessageChain.isStream()) {
                // Streaming response
                blockingRequester.launch {
                    val messageCounter = AtomicLong(-1L)

                    val onNewTokenReceived: ChatStreamCallback = {
                        blockingRequester.launch {
                            lock.withLock {
                                nodeNettyClient.sendMessage(
                                    messageTemplate.copy(messages = listOf(
                                        ChatResponseMessage(
                                            success = true,
                                            message = messageCounter.incrementAndGet().toString(),
                                            content = it,
                                            generatedTokens = 0,
                                            totalTokens = 0
                                        )
                                    ))
                                )
                            }
                        }
                    }

                    val onCompleted: ChatStreamCompletedCallback = { _, generatedTokens, totalTokens ->
                        Global.unlockChatRunningStatus(sessionId)

                        blockingRequester.launch {
                            lock.withLock {
                                nodeNettyClient.sendMessage(
                                    messageTemplate.copy(messages = listOf(
                                        ChatResponseMessage(
                                            success = true,
                                            message = GlobalConstants.Flags.STREAMING_MESSAGE_FINISHED,
                                            content = null,
                                            generatedTokens = generatedTokens,
                                            totalTokens = totalTokens
                                        )
                                    ))
                                )
                            }
                        }
                    }

                    val onFailed: ChatStreamRequestFailedCallback = {
                        Global.unlockChatRunningStatus(sessionId)

                        blockingRequester.launch {
                            lock.withLock {
                                nodeNettyClient.sendMessage(
                                    messageTemplate.copy(messages = listOf(
                                        ChatResponseMessage(
                                            success = false,
                                            message = it.message,
                                            content = null,
                                            generatedTokens = 0,
                                            totalTokens = 0
                                        )
                                    ))
                                )
                            }
                        }
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
            } else {
                // Blocking response
                blockingRequester.launch {
                    val result = when (task.type) {
                        ChatTask.Type.OLLAMA -> {
                             ollamaChatService.blockingGenerate(task.prompts, task.chatOptions as OllamaChatOptions?)
                        }
                        ChatTask.Type.DEEPSEEK -> {
                            deepSeekChatService.blockingGenerate(task.prompts, task.chatOptions as DeepSeekChatOptions?)
                        }
                    }

                    Global.unlockChatRunningStatus(sessionId)

                    lock.withLock {
                        nodeNettyClient.sendMessage(
                            messageTemplate.copy(messages = listOf(
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
                                    ChatResponseMessage(
                                        success = false,
                                        message = result.message,
                                        content = null,
                                        generatedTokens = 0,
                                        totalTokens = 0
                                    )
                                }
                            ))
                        )
                    }
                }
            }
        }
    }
}