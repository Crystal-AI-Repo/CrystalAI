package com.lovelycatv.ai.crystal.dispatcher.response

class StreamChatRequestResult(
    isRequestSent: Boolean,
    isSuccess: Boolean,
    message: String,
    sessionId: String?
) : ChatRequestResult(isRequestSent, isSuccess, message, sessionId)