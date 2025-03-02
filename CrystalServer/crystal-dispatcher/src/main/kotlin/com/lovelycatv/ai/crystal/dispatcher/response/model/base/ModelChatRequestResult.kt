package com.lovelycatv.ai.crystal.dispatcher.response.model.base

import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage

/**
 * @author lovelycat
 * @since 2025-03-03 02:40
 * @version 1.0
 */
abstract class ModelChatRequestResult(
    isRequestSent: Boolean,
    isSuccess: Boolean,
    message: String,
    sessionId: String?,
    streamId: String?,
    val results: List<ChatResponseMessage>
) : ModelRequestResult(isRequestSent, isSuccess, message, sessionId, streamId)