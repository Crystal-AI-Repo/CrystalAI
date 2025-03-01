package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.dispatcher.response.OneTimeEmbeddingRequestResult

/**
 * @author lovelycat
 * @since 2025-03-01 20:55
 * @version 1.0
 */
abstract class EmbeddingService<OPTIONS: AbstractEmbeddingOptions> : ModelRequestService() {
    /**
     * Send a embedding request to node
     *
     * @param options EmbeddingOptions
     * @param messages Prompt Messages
     * @param ignoreResult If true, the result will be ignored
     * @param timeout Task timeout
     * @return [OneTimeEmbeddingRequestResult]
     */
    abstract suspend fun sendOneTimeEmbeddingTask(
        options: OPTIONS,
        messages: List<PromptMessage>,
        ignoreResult: Boolean,
        timeout: Long
    ): OneTimeEmbeddingRequestResult
}