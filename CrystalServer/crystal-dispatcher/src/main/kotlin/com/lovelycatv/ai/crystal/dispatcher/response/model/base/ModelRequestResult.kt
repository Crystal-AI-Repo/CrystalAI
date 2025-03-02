package com.lovelycatv.ai.crystal.dispatcher.response.model.base

/**
 * @author lovelycat
 * @since 2025-02-28 22:02
 * @version 1.0
 */
open class ModelRequestResult(
    val isRequestSent: Boolean,
    val isSuccess: Boolean,
    val message: String,
    val sessionId: String?,
    val streamId: String?
)