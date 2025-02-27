package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.dispatcher.data.node.OneTimeChatRequestResult

/**
 * @author lovelycat
 * @since 2025-02-27 00:19
 * @version 1.0
 */
interface OllamaChatService {
    /**
     * Send a chat request, and the response is given after the node has completely finished generating the text.
     *
     * @param options [OllamaChatOptions]
     * @param messages List of [PromptMessage]
     * @param ignoreResult If true, the response of node will be ignored.
     * @param timeout Request timeout
     * @return [OneTimeChatRequestResult]
     */
    suspend fun sendOneTimeChatTask(
        options: OllamaChatOptions?,
        messages: List<PromptMessage>,
        ignoreResult: Boolean,
        timeout: Long
    ): OneTimeChatRequestResult
}