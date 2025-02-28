package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.chat.options.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.dispatcher.response.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.StreamChatRequestResult

/**
 * @author lovelycat
 * @since 2025-02-27 00:19
 * @version 1.0
 */
interface ChatService<OPTIONS: AbstractChatOptions> {
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
        options: OPTIONS,
        messages: List<PromptMessage>,
        ignoreResult: Boolean,
        timeout: Long
    ): OneTimeChatRequestResult

    /**
     * Send a stream chat request, every new generated token will be sent to dispatcher.
     *
     * @param options [OllamaChatOptions]
     * @param messages List of [PromptMessage]
     * @param timeout Request timeout
     * @return [StreamChatRequestResult]
     */
    suspend fun sendStreamChatTask(
        options: OPTIONS,
        messages: List<PromptMessage>,
        timeout: Long
    ): StreamChatRequestResult
}