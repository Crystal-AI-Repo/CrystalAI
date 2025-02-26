package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.common.netty.sendMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.suspendTimeoutCoroutine
import com.lovelycatv.ai.crystal.dispatcher.OllamaTaskDispatcher
import com.lovelycatv.ai.crystal.dispatcher.OllamaTaskManager
import com.lovelycatv.ai.crystal.dispatcher.data.node.OllamaChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.data.node.OllamaChatRequestSessionContainer
import org.springframework.stereotype.Service
import kotlin.coroutines.resume

/**
 * @author lovelycat
 * @since 2025-02-27 00:19
 * @version 1.0
 */
@Service
class OllamaChatServiceImpl(
    private val ollamaTaskDispatcher: OllamaTaskDispatcher,
    private val ollamaTaskManager: OllamaTaskManager
) : OllamaChatService {
    private val logger = logger()

    /**
     * Send a chat request, and the response is given after the node has completely finished generating the text.
     *
     * @param options [OllamaChatOptions]
     * @param messages List of [PromptMessage]
     * @param ignoreResult If true, the response of node will be ignored.
     * @return [OllamaChatRequestResult]
     */
    override suspend fun sendOneTimeChatTask(
        options: OllamaChatOptions?,
        messages: List<PromptMessage>,
        ignoreResult: Boolean
    ): OllamaChatRequestResult {
        val node = ollamaTaskDispatcher.requireAvailableNode()
        return if (node != null) {
            if (node.channel != null) {
                val sessionId = ollamaTaskDispatcher.requireSessionId()

                logger.info("Sending OneTimeChatTask to node: [${node.nodeName}], sessionId: [${sessionId}]")

                val message = MessageChainBuilder {
                    // Random sessionId
                    this.sessionId(sessionId)
                    // No streaming
                    this.streamId(null)

                    // Add OllamaChatOptions is non-null
                    options?.let {
                        this.addMessage(it)
                    }

                    // Add all messages
                    this.addMessages(messages)
                }

                val result = node.channel.sendMessage(message)

                if (result) {
                    if (!ignoreResult) {
                        var lastRecordedContainer: OllamaChatRequestSessionContainer? = null
                        val nodeResponse = suspendTimeoutCoroutine(16000) { continuation ->
                            ollamaTaskManager.pushSession(
                                recipient = node,
                                messageChain = message,
                                callbacks = object : OllamaChatRequestSessionContainer.Callbacks {
                                    override fun onReceived(message: OllamaChatResponseMessage, container: OllamaChatRequestSessionContainer) {
                                        lastRecordedContainer = container
                                    }
                                    override fun onFinished(container: OllamaChatRequestSessionContainer) {
                                        continuation.resume(container.getResponses())
                                    }
                                }
                            )
                        }
                        if (nodeResponse != null) {
                            OllamaChatRequestResult(
                                isRequestSent = true,
                                isSuccess = true,
                                results = nodeResponse
                            )
                        } else {
                            OllamaChatRequestResult(isRequestSent = true, isSuccess = false, results = lastRecordedContainer?.getResponses() ?: listOf())
                        }
                    } else {
                        OllamaChatRequestResult(isRequestSent = true, isSuccess = true)
                    }
                } else {
                    OllamaChatRequestResult(isRequestSent = false, isSuccess = false)
                }
            } else {
                OllamaChatRequestResult(isRequestSent = false, isSuccess = false)
            }
        } else {
            OllamaChatRequestResult(isRequestSent = false, isSuccess = false)
        }
    }

}