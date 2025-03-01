package com.lovelycatv.ai.crystal.dispatcher.response

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage

class OneTimeEmbeddingRequestResult(
    isRequestSent: Boolean,
    isSuccess: Boolean,
    message: String,
    sessionId: String?,
    val results: List<EmbeddingResponseMessage> = listOf()
) : ModelRequestResult(isRequestSent, isSuccess, message, sessionId)