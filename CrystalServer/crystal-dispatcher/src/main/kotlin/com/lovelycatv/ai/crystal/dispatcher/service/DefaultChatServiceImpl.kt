package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.dispatcher.response.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.response.ModelRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.StreamChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.task.AbstractChatTask
import com.lovelycatv.ai.crystal.dispatcher.task.dispatcher.TaskDispatcher
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import com.lovelycatv.ai.crystal.dispatcher.task.OneTimeChatTask
import com.lovelycatv.ai.crystal.dispatcher.task.StreamChatTask
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
class DefaultChatServiceImpl(
    private val taskDispatcher: TaskDispatcher,
    private val taskManager: TaskManager
) : DefaultChatService() {
    /**
     * Send a chat request, and the response is given after the node has completely finished generating the text.
     *
     * @param options [OllamaChatOptions]
     * @param messages List of [PromptMessage]
     * @param ignoreResult If true, the response of node will be ignored.
     * @param timeout Request timeout
     * @return [OneTimeChatRequestResult]
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun sendOneTimeChatTask(
        options: AbstractChatOptions,
        messages: List<PromptMessage>,
        ignoreResult: Boolean,
        timeout: Long
    ): OneTimeChatRequestResult {
        return sendTask(taskDispatcher, taskManager, OneTimeChatTask(options, messages, timeout), ignoreResult) { it, args ->
            OneTimeChatRequestResult(
                it.isRequestSent,
                it.isSuccess,
                it.message,
                it.sessionId,
                results = if (args.isNotEmpty())
                    args[0] as List<ChatResponseMessage>
                else emptyList()
            )
        }
    }

    /**
     * Send a stream chat request, every new generated token will be sent to dispatcher.
     *
     * @param options [OllamaChatOptions]
     * @param messages List of [PromptMessage]
     * @param timeout Request timeout
     * @return [StreamChatRequestResult]
     */
    override suspend fun sendStreamChatTask(options: AbstractChatOptions, messages: List<PromptMessage>, timeout: Long): StreamChatRequestResult {
        return sendTask(taskDispatcher, taskManager, StreamChatTask(options, messages, timeout), true) { it, args ->
            StreamChatRequestResult(it.isRequestSent, it.isSuccess, it.message, it.sessionId)
        }
    }
}