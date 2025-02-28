package com.lovelycatv.ai.crystal.dispatcher.response

import com.lovelycatv.ai.crystal.common.data.message.chat.ChatResponseMessage

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
    val results: List<ChatResponseMessage> = listOf()
) : ChatRequestResult(isRequestSent, isSuccess, message, sessionId)