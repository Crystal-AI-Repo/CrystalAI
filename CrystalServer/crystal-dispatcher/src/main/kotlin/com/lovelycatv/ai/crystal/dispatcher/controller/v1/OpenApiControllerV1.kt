package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.OpenApiController.CHAT_COMPLETION
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.dispatcher.data.ChatCompletionMessage
import com.lovelycatv.ai.crystal.dispatcher.data.ChatCompletionPayloads
import com.lovelycatv.ai.crystal.dispatcher.data.ChatCompletionResponse
import com.lovelycatv.ai.crystal.dispatcher.data.StreamChatCompletionResponse
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.response.model.base.ModelChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.model.chat.StreamChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.service.DefaultChatService
import com.lovelycatv.ai.crystal.dispatcher.task.manager.ListenableTaskManager
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author lovelycat
 * @since 2025-03-03 01:22
 * @version 1.0
 */
@RestController
class OpenApiControllerV1(
    private val chatService: DefaultChatService,
    private val taskManager: TaskManager
) : IOpenApiControllerV1 {
    private val objectMapper = jacksonObjectMapper().apply {
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    private val chatCompletionRequestHandler = CoroutineScope(Dispatchers.IO)

    @Async
    @PostMapping(CHAT_COMPLETION, produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    override suspend fun chatCompletion(
        @RequestBody payloads: ChatCompletionPayloads
    ): CorePublisher<String> {
        val options = buildChatOptions(payloads.model)
        val messages = payloads.messages.map {
            PromptMessage(
                role = if (it.role.lowercase() == "system") {
                    PromptMessage.Role.SYSTEM
                } else if (it.role.lowercase() == "user") {
                    PromptMessage.Role.USER
                } else if (it.role.lowercase() == "assistant") {
                    PromptMessage.Role.ASSISTANT
                } else throw IllegalStateException(""),
                message = listOf(PromptMessage.Content.fromString(it.content))
            )
        }

        return if (payloads.stream) {
            Flux.create { emitter ->
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
                                    result.appendResults(message).toStreamChatCompletionResponse(payloads, false)
                                        .toJSONString(objectMapper)
                                )
                            }

                            override fun onFinished(container: ChatRequestSessionContainer) {
                                container.recentReceived()?.let { lastReceived ->
                                    emitter.next(
                                        result.appendResults(lastReceived)
                                            .toStreamChatCompletionResponse(payloads, true).toJSONString(objectMapper)
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
        } else {
            // Blocking request
            val result = chatService.sendOneTimeChatTask(
                options = options,
                messages = messages,
                ignoreResult = false,
                timeout = 600000
            )

            Mono.just(
                if (result.isSuccess) {
                    result.toChatCompletionResponse(payloads).toJSONString(objectMapper)
                } else {
                    Result.badRequest(result.message).toJSONString()
                }
            )
        }
    }

    private fun buildChatOptions(model: String): AbstractChatOptions {
        val (platformName, modelName) = model.split("@")

        return when (platformName.lowercase()) {
            "ollama" -> OllamaChatOptions(modelName = modelName, temperature = null)
            "deepseek" -> DeepSeekChatOptions(modelName = modelName, temperature = null)
            else -> throw IllegalStateException("$platformName is not supported yet.")
        }
    }

    private fun ModelChatRequestResult.toChatCompletionResponse(payloads: ChatCompletionPayloads): ChatCompletionResponse {
        val totalTokens = this.results.sumOf { it.totalTokens }
        val completionTokens = this.results.sumOf { it.generatedTokens }

        return ChatCompletionResponse(
            id = this.sessionId!!,
            created = System.currentTimeMillis() / 1000,
            model = payloads.model,
            choices = this.results.mapIndexed { index, it ->
                ChatCompletionResponse.Choice(
                    index = index,
                    message = ChatCompletionMessage(
                        role = "assistant",
                        content = it.content ?: ""
                    ),
                    finishReason = "stop"
                )
            },
            usage = ChatCompletionResponse.Usage(
                totalTokens = totalTokens,
                completionTokens = completionTokens,
                promptTokens = totalTokens - completionTokens
            )
        )
    }

    private fun ModelChatRequestResult.toStreamChatCompletionResponse(payloads: ChatCompletionPayloads, finally: Boolean): StreamChatCompletionResponse {
        val totalTokens = this.results.sumOf { it.totalTokens }
        val completionTokens = this.results.sumOf { it.generatedTokens }

        return StreamChatCompletionResponse(
            id = this.streamId!!,
            choices = this.results.mapIndexed { index, it ->
                StreamChatCompletionResponse.Choice(
                    index = index,
                    delta = StreamChatCompletionResponse.Choice.Delta(
                        content = it.content ?: ""
                    ),
                    finishReason = if (finally) "stop" else null
                )
            },
            usage = if (finally) StreamChatCompletionResponse.Usage(
                totalTokens = totalTokens,
                completionTokens = completionTokens,
                promptTokens = totalTokens - completionTokens
            ) else null
        )
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