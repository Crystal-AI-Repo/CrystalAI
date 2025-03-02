package com.lovelycatv.ai.crystal.dispatcher.response.model.chat

import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.dispatcher.response.model.base.ModelChatRequestResult

/**
 * @author lovelycat
 * @since 2025-02-27 00:34
 * @version 1.0
 */
class OneTimeChatRequestResult(
    isRequestSent: Boolean,
    isSuccess: Boolean,
    message: String,
    sessionId: String?,
    streamId: String?,
    results: List<ChatResponseMessage>
) : ModelChatRequestResult(isRequestSent, isSuccess, message, sessionId, streamId, results)