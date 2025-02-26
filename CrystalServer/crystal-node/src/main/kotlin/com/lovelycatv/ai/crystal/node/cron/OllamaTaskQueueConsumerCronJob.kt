package com.lovelycatv.ai.crystal.node.cron

import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.netty.AbstractNodeNettyClient
import com.lovelycatv.ai.crystal.node.queue.OllamaTaskQueue
import com.lovelycatv.ai.crystal.node.service.OllamaChatService
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.math.log

/**
 * @author lovelycat
 * @since 2025-02-26 22:08
 * @version 1.0
 */
@Component
@EnableScheduling
class OllamaTaskQueueConsumerCronJob(
    private val ollamaTaskQueue: OllamaTaskQueue,
    private val ollamaChatService: OllamaChatService,
    private val nodeNettyClient: AbstractNodeNettyClient
) {
    private val logger = logger()

    @Scheduled(cron = "0/1 * * * * ?")
    fun consume() {
        val task = ollamaTaskQueue.requireTask()
        if (task != null) {
            logger.info("OllamaTask consumed, sessionId: ${task.requesterSessionId}, maxExecTime: ${task.expireTime}ms")

            val messageTemplate = MessageChainBuilder {
                // Copy the sessionId
                this.sessionId(task.requesterSessionId)

                // No streaming
                this.streamId(null)
            }

            if (task.originalMessageChain.isStream()) {
                // Streaming response
                ollamaChatService.streamGenerate(
                    content = task.prompts,
                    options = task.chatOptions,
                    onNewTokenReceived = {
                        nodeNettyClient.sendMessage(
                            messageTemplate.copy(messages = listOf(
                                OllamaChatResponseMessage(
                                    success = true,
                                    message = "",
                                    content = it,
                                    generatedTokens = 0,
                                    totalTokens = 0
                                )
                            ))
                        )
                    },
                    onCompleted = { _, generatedTokens, totalTokens ->
                        nodeNettyClient.sendMessage(
                            messageTemplate.copy(messages = listOf(
                                OllamaChatResponseMessage(
                                    success = true,
                                    message = GlobalConstants.Flags.STREAMING_MESSAGE_FINISHED,
                                    content = null,
                                    generatedTokens = generatedTokens,
                                    totalTokens = totalTokens
                                )
                            ))
                        )
                    }
                )
            } else {
                // Blocking response
                val response = ollamaChatService.blockingGenerate(
                    content = task.prompts,
                    options = task.chatOptions
                )

                nodeNettyClient.sendMessage(
                    messageTemplate.copy(messages = listOf(
                        OllamaChatResponseMessage(
                            success = true,
                            message = "",
                            content = response.result?.output?.content,
                            generatedTokens = response.metadata.usage.generationTokens,
                            totalTokens = response.metadata.usage.totalTokens
                        )
                    ))
                )
            }
        }
    }
}