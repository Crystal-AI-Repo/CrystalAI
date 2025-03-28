package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.OpenApiController.CHAT_COMPLETION
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.OpenApiController.EMBEDDINGS
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.dispatcher.data.*
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.response.model.base.ModelChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.model.chat.StreamChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.service.DefaultChatService
import com.lovelycatv.ai.crystal.dispatcher.service.DefaultEmbeddingService
import com.lovelycatv.ai.crystal.dispatcher.task.manager.ListenableTaskManager
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import com.lovelycatv.ai.crystal.openapi.AbstractOpenApiController
import com.lovelycatv.ai.crystal.openapi.dto.ChatCompletionApiRequest
import com.lovelycatv.ai.crystal.openapi.dto.EmbeddingApiRequest
import com.lovelycatv.ai.crystal.openapi.dto.StreamChatCompletionResponse
import com.lovelycatv.ai.crystal.openapi.plugin.ChatOptionsBuilder
import com.lovelycatv.ai.crystal.openapi.plugin.EmbeddingOptionsBuilder
import com.lovelycatv.ai.crystal.openapi.toStreamChatCompletionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

/**
 * @author lovelycat
 * @since 2025-03-03 01:22
 * @version 1.0
 */
@RestController
class OpenApiControllerV1(
        private val chatService: DefaultChatService,
        private val embeddingService: DefaultEmbeddingService,
        private val taskManager: TaskManager,
        chatOptionsBuilders: List<ChatOptionsBuilder<*>>,
        embeddingOptionsBuilders: List<EmbeddingOptionsBuilder<*>>
) : AbstractOpenApiController(chatOptionsBuilders, embeddingOptionsBuilders) {
    private val objectMapper = jacksonObjectMapper().apply {
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    private val chatCompletionRequestHandler = CoroutineScope(Dispatchers.IO)

    @Async
    @PostMapping(CHAT_COMPLETION, produces = [MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE])
    override suspend fun chatCompletion(payloads: ChatCompletionApiRequest): Any {
        return super.chatCompletion(payloads)
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
        // Blocking request
        val result = chatService.sendOneTimeChatTask(
            options = options,
            messages = messages,
            ignoreResult = false,
            timeout = 600000
        )

        return if (result.isSuccess) {
            Triple(result.results, result.sessionId ?: "", result.message)
        } else {
            Triple(null, result.sessionId ?: "", result.message)
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
        return Flux.create { emitter ->
            chatCompletionRequestHandler.launch {
                val result = chatService.sendStreamChatTask(
                    options = options,
                    messages = messages,
                    timeout = 600000
                )

                if (result.isSuccess) {
                    taskManager.subscribe(result.sessionId!!, object : ListenableTaskManager.SimpleSubscriber {
                        override fun onReceived(
                            container: ChatRequestSessionContainer,
                            message: ModelResponseMessage
                        ) {
                            emitter.next(
                                result.appendResults(message).run {
                                    this.results.toStreamChatCompletionResponse(this.sessionId ?: "", false)
                                        .toJSONString(objectMapper)
                                }
                            )
                        }

                        override fun onFinished(container: ChatRequestSessionContainer) {
                            container.recentReceived()?.let { lastReceived ->
                                emitter.next(
                                    result.appendResults(lastReceived).run {
                                        this.results.toStreamChatCompletionResponse(this.sessionId ?: "", true)
                                            .toJSONString(objectMapper)
                                    }

                                )
                            }
                            emitter.next("[DONE]")
                            emitter.complete()
                        }

                        override fun onFailed(
                            container: ChatRequestSessionContainer?,
                            failedMessage: ModelResponseMessage?
                        ) {
                            emitter.next(Result.badRequest(failedMessage?.message ?: "").toJSONString())
                            emitter.complete()
                        }
                    })
                } else {
                    emitter.next(Result.badRequest(result.message).toJSONString())
                    emitter.complete()
                }

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
        val response = embeddingService.sendOneTimeEmbeddingTask(
            options = options,
            messages = messages,
            ignoreResult = false,
            timeout = 120000
        )

        return response.results.firstOrNull() to response.message
    }

    private fun StreamChatRequestResult.appendResults(vararg result: ModelResponseMessage): StreamChatRequestResult {
        return StreamChatRequestResult(
            this.isRequestSent,
            this.isSuccess,
            this.message,
            this.sessionId,
            this.streamId,
            this.results + result.filterIsInstance<ChatResponseMessage>().toList()
        )
    }
}