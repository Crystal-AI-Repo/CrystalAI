package com.lovelycatv.ai.crystal.dispatcher.controller

import com.lovelycatv.ai.crystal.dispatcher.data.ChatCompletionPayloads
import com.lovelycatv.ai.crystal.dispatcher.data.ChatCompletionResponse
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.context.request.async.DeferredResult
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author lovelycat
 * @since 2025-03-03 01:22
 * @version 1.0
 */
interface IOpenApiController {
    @Async
    suspend fun chatCompletion(@RequestBody payloads: ChatCompletionPayloads): CorePublisher<String>
}