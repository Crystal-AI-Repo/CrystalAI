package com.lovelycatv.crystal.openapi

import com.lovelycatv.crystal.openapi.dto.ChatCompletionApiRequest
import com.lovelycatv.crystal.openapi.dto.EmbeddingApiRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.RequestBody

/**
 * @author lovelycat
 * @since 2025-03-03 01:22
 * @version 1.0
 */
interface IOpenApiController {
    @Async
    suspend fun chatCompletion(
        @RequestBody payloads: ChatCompletionApiRequest
    ): Any

    @Async
    suspend fun embedding(
        @RequestBody payloads: EmbeddingApiRequest
    ): Any
}