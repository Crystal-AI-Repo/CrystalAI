package com.lovelycatv.ai.crystal.dispatcher.response.model.embedding

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.dispatcher.response.model.base.ModelRequestResult

class OneTimeEmbeddingRequestResult(
    isRequestSent: Boolean,
    isSuccess: Boolean,
    message: String,
    sessionId: String?,
    streamId: String?,
    val results: List<EmbeddingResponseMessage> = listOf()
) : ModelRequestResult(isRequestSent, isSuccess, message, sessionId, streamId)