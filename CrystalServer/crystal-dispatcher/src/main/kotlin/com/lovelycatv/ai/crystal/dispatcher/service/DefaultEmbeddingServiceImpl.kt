package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.dispatcher.response.OneTimeEmbeddingRequestResult
import com.lovelycatv.ai.crystal.dispatcher.task.OneTimeEmbeddingTask
import com.lovelycatv.ai.crystal.dispatcher.task.TaskPerformResult
import com.lovelycatv.ai.crystal.dispatcher.task.dispatcher.TaskDispatcher
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-03-01 20:56
 * @version 1.0
 */
@Service
class DefaultEmbeddingServiceImpl(
    private val taskDispatcher: TaskDispatcher,
    private val taskManager: TaskManager
) : DefaultEmbeddingService() {
    /**
     * Send a embedding request to node
     *
     * @param options EmbeddingOptions
     * @param messages Prompt Messages
     * @param ignoreResult If true, the result will be ignored
     * @param timeout Task timeout
     * @return [OneTimeEmbeddingRequestResult]
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun sendOneTimeEmbeddingTask(
        options: AbstractEmbeddingOptions,
        messages: List<PromptMessage>,
        ignoreResult: Boolean,
        timeout: Long
    ): OneTimeEmbeddingRequestResult {
        return super.sendTask(taskDispatcher, taskManager, OneTimeEmbeddingTask(options, messages, timeout), false) { rawResult, args ->
            OneTimeEmbeddingRequestResult(
                rawResult.isRequestSent,
                rawResult.isSuccess,
                rawResult.message,
                rawResult.sessionId,
                results = if (args.isNotEmpty())
                    args[0] as List<EmbeddingResponseMessage>
                else emptyList()
            )
        }
    }
}