package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.dispatcher.response.model.chat.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.model.chat.StreamChatRequestResult

/**
 * @author lovelycat
 * @since 2025-02-27 00:19
 * @version 1.0
 */
abstract class ChatService<OPTIONS: AbstractChatOptions> : ModelRequestService() {
    /**
     * Send a chat request, and the response is given after the node has completely finished generating the text.
     *
     * @param options [OllamaChatOptions]
     * @param messages List of [PromptMessage]
     * @param ignoreResult If true, the response of node will be ignored.
     * @param timeout Request timeout
     * @return [OneTimeChatRequestResult]
     */
    abstract suspend fun sendOneTimeChatTask(
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
    abstract suspend fun sendStreamChatTask(
        options: OPTIONS,
        messages: List<PromptMessage>,
        timeout: Long
    ): StreamChatRequestResult
}