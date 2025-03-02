package com.lovelycatv.ai.crystal.dispatcher.response.model.chat

import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.dispatcher.response.model.base.ModelChatRequestResult

class StreamChatRequestResult(
    isRequestSent: Boolean,
    isSuccess: Boolean,
    message: String,
    sessionId: String?,
    streamId: String?,
    results: List<ChatResponseMessage>
) : ModelChatRequestResult(isRequestSent, isSuccess, message, sessionId, streamId, results)