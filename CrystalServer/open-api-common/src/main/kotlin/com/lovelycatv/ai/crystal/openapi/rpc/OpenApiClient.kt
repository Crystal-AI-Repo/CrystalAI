package com.lovelycatv.ai.crystal.openapi.rpc

import com.lovelycatv.ai.crystal.common.client.IFeignClient
import com.lovelycatv.ai.crystal.openapi.dto.ChatCompletionApiRequest
import com.lovelycatv.ai.crystal.openapi.dto.EmbeddingApiRequest
import org.springframework.web.bind.annotation.PostMapping

/**
 * @author lovelycat
 * @since 2025-03-29 01:54
 * @version 1.0
 */
interface OpenApiClient : IFeignClient {
    @PostMapping("/chat/completion")
    fun chatCompletion(payloads: ChatCompletionApiRequest): Map<String, Any?>

    @PostMapping("/chat/embeddings")
    fun embeddings(payloads: EmbeddingApiRequest): Map<String, Any?>
}