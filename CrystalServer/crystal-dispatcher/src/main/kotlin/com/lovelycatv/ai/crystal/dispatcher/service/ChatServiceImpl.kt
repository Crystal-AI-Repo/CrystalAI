package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.chat.options.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.dispatcher.data.node.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.task.dispatcher.TaskDispatcher
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import com.lovelycatv.ai.crystal.dispatcher.task.OneTimeChatTask
import com.lovelycatv.ai.crystal.dispatcher.task.TaskPerformResult
import com.lovelycatv.ai.crystal.dispatcher.task.manager.ListenableTaskManager
import org.springframework.stereotype.Service
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author lovelycat
 * @since 2025-02-27 00:19
 * @version 1.0
 */
@Service
class ChatServiceImpl(
    private val taskDispatcher: TaskDispatcher,
    private val taskManager: TaskManager
) : DefaultChatService {
    private val logger = logger()

    /**
     * Send a chat request, and the response is given after the node has completely finished generating the text.
     *
     * @param options [OllamaChatOptions]
     * @param messages List of [PromptMessage]
     * @param ignoreResult If true, the response of node will be ignored.
     * @param timeout Request timeout
     * @return [OneTimeChatRequestResult]
     */
    override suspend fun sendOneTimeChatTask(
        options: AbstractChatOptions?,
        messages: List<PromptMessage>,
        ignoreResult: Boolean,
        timeout: Long
    ): OneTimeChatRequestResult {
        val result = taskDispatcher.performTask(OneTimeChatTask(options, messages, timeout))

        if (result != null) {
            when (result.status) {
                TaskPerformResult.Status.SUCCESS -> {
                    val sessionId = result.data
                    if (!ignoreResult) {
                        return suspendCoroutine { continuation ->
                            taskManager.subscribe(sessionId, object : ListenableTaskManager.SimpleSubscriber {
                                override fun onReceived(container: ChatRequestSessionContainer, message: ChatResponseMessage) {}

                                override fun onFinished(container: ChatRequestSessionContainer) {
                                    continuation.resume(OneTimeChatRequestResult(
                                        isRequestSent = true,
                                        isSuccess = true,
                                        message = "",
                                        results = container.getResponses()
                                    ))
                                }

                                override fun onFailed(container: ChatRequestSessionContainer?, failedMessage: ChatResponseMessage?) {
                                    continuation.resume(OneTimeChatRequestResult(
                                        isRequestSent = true,
                                        isSuccess = false,
                                        message = failedMessage?.message ?: "",
                                        results = container?.getResponses() ?: emptyList()
                                    ))
                                }
                            })

                            taskManager.subscribe(sessionId, ListenableTaskManager.OnTimeoutSubscriber {
                                continuation.resume(OneTimeChatRequestResult(isRequestSent = true, isSuccess = false, message = "Timeout"))
                            })
                        }
                    } else {
                        return OneTimeChatRequestResult(isRequestSent = true, isSuccess = true, message = "")
                    }
                }
                TaskPerformResult.Status.FAILED -> {
                    return OneTimeChatRequestResult(isRequestSent = false, isSuccess = false, message = result.message ?: "")
                }
            }
        } else {
            return OneTimeChatRequestResult(isRequestSent = false, isSuccess = false, message = "No available node")
        }
    }

}