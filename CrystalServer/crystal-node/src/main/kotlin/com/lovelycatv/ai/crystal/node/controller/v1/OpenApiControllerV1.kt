package com.lovelycatv.ai.crystal.node.controller.v1

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.OpenApiController.CHAT_COMPLETION
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.OpenApiController.EMBEDDINGS
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.exception.UnsupportedModelOptionsType
import com.lovelycatv.ai.crystal.node.service.ServiceDispatcher
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamCompletedCallback
import com.lovelycatv.ai.crystal.node.service.chat.base.ChatStreamRequestFailedCallback
import com.lovelycatv.crystal.openapi.AbstractOpenApiController
import com.lovelycatv.crystal.openapi.dto.ChatCompletionApiRequest
import com.lovelycatv.crystal.openapi.dto.EmbeddingApiRequest
import com.lovelycatv.crystal.openapi.plugin.ChatOptionsBuilder
import com.lovelycatv.crystal.openapi.plugin.EmbeddingOptionsBuilder
import com.lovelycatv.crystal.openapi.toStreamChatCompletionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lovelycat
 * @since 2025-03-28 19:14
 * @version 1.0
 */
@RestController
@RequestMapping
@ConditionalOnProperty(name = ["crystal.mode"], havingValue = "standalone", matchIfMissing = false)
class OpenApiControllerV1(
    private val serviceDispatcher: ServiceDispatcher,
    chatOptionsBuilders: List<ChatOptionsBuilder<*>>,
    embeddingOptionsBuilders: List<EmbeddingOptionsBuilder<*>>
) : AbstractOpenApiController(chatOptionsBuilders, embeddingOptionsBuilders), InitializingBean {
    private val logger = logger()

    private val objectMapper = jacksonObjectMapper().apply {
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    private val chatCompletionRequestHandler = CoroutineScope(Dispatchers.IO)

    override fun afterPropertiesSet() {
        logger.info("Open API for this node is enabled.")
    }

    @Async
    @PostMapping(CHAT_COMPLETION, produces = [MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE])
    override suspend fun chatCompletion(payloads: ChatCompletionApiRequest): Any {
        return super.chatCompletion(payloads) as Mono<*>
    }

    @Async
    @PostMapping(EMBEDDINGS)
    override suspend fun embedding(payloads: EmbeddingApiRequest): Any {
        return super.embedding(payloads)
    }

    /**
     * Actual implementation of chat completion
     *
     * @param options [AbstractChatOptions]
     * @param messages List of [PromptMessage]
     * @return
     */
    override suspend fun doChatCompletion(options: AbstractChatOptions, messages: List<PromptMessage>): Triple<List<ChatResponseMessage>?, String, String> {
        val service = serviceDispatcher.determineChatService(options::class)
            ?: throw UnsupportedModelOptionsType(options::class.qualifiedName)

        val result = service.blockingGenerate(messages, options)

        if (result.success) {
            return Triple(
                listOf(
                    ChatResponseMessage(
                        success = true,
                        message = GlobalConstants.Flags.MESSAGE_FINISHED,
                        content = result.data!!.results.firstOrNull()?.content,
                        generatedTokens = result.data.metadata.generatedTokens,
                        totalTokens = result.data.metadata.totalTokens
                    )
                ),
                UUID.randomUUID().toString(),
                result.message ?: ""
            )
        } else {
            return Triple(listOf(ChatResponseMessage.failed(result.message ?: "")), UUID.randomUUID().toString(), result.message ?: "")
        }
    }

    /**
     * Actual implementation of stream chat completion
     *
     * @param options [AbstractChatOptions]
     * @param messages List of [PromptMessage]
     * @return
     */
    override fun doStreamChatCompletion(options: AbstractChatOptions, messages: List<PromptMessage>): Flux<*> {
        val service = serviceDispatcher.determineChatService(options::class)
            ?: throw UnsupportedModelOptionsType(options::class.qualifiedName)

        val sessionId = UUID.randomUUID().toString()

        // Streaming response
        val messageCounter = AtomicLong(-1L)

        return Flux.create { emitter ->
            chatCompletionRequestHandler.launch {
                val onNewTokenReceived: ChatStreamCallback = {
                    emitter.next(
                        listOf(ChatResponseMessage(
                            success = true,
                            message = messageCounter.incrementAndGet().toString(),
                            content = it,
                            generatedTokens = 0,
                            totalTokens = 0
                        )).toStreamChatCompletionResponse(sessionId, false).toJSONString(objectMapper)
                    )
                }

                val onCompleted: ChatStreamCompletedCallback = { _, generatedTokens, totalTokens ->
                    emitter.next(
                        listOf(ChatResponseMessage(
                            success = true,
                            message = GlobalConstants.Flags.STREAMING_MESSAGE_FINISHED,
                            content = null,
                            generatedTokens = generatedTokens,
                            totalTokens = totalTokens
                        )).toStreamChatCompletionResponse(sessionId, true)
                            .toJSONString(objectMapper)
                    )
                    emitter.next("[DONE]")
                    emitter.complete()
                }


                val onFailed: ChatStreamRequestFailedCallback = {
                    Global.unlockTaskRunningStatus(sessionId)

                    emitter.next(Result.badRequest(it.message ?: "").toJSONString())
                    emitter.complete()
                }

                service.streamGenerate(messages, options, onNewTokenReceived, onFailed, onCompleted)
            }
        }

    }

    /**
     * Actual implementation of embedding
     *
     * @param options [AbstractEmbeddingOptions]
     * @param messages List of [PromptMessage]
     * @return
     */
    override suspend fun doEmbedding(options: AbstractEmbeddingOptions, messages: List<PromptMessage>): Pair<EmbeddingResponseMessage?, String> {
        val service = serviceDispatcher.determineEmbeddingService(options::class)
            ?: throw UnsupportedModelOptionsType(options::class.qualifiedName)

        val result = service.embedding(options, messages)

        return EmbeddingResponseMessage.success(
            GlobalConstants.Flags.MESSAGE_FINISHED,
            results = result.results,
            promptTokens = result.metadata.promptTokens,
            totalTokens = result.metadata.totalTokens
        ) to ""
    }
}