package com.lovelycatv.crystal.openapi

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.crystal.openapi.dto.*
import com.lovelycatv.crystal.openapi.exception.PlatformNotSupportException
import com.lovelycatv.crystal.openapi.plugin.ChatOptionsBuilder
import com.lovelycatv.crystal.openapi.plugin.EmbeddingOptionsBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author lovelycat
 * @since 2025-03-28 18:13
 * @version 1.0
 */
abstract class AbstractOpenApiController(
    private val chatOptionsBuilders: List<ChatOptionsBuilder<*>>,
    private val embeddingOptionsBuilders: List<EmbeddingOptionsBuilder<*>>
) : IOpenApiController {
    private val objectMapper = jacksonObjectMapper().apply {
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    override suspend fun chatCompletion(payloads: ChatCompletionApiRequest): Any {
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
            this.doStreamChatCompletion(options, messages)
        } else {
            // Blocking request
            val (result, sessionId, message) = this.doChatCompletion(options, messages)

            Mono.just(
                (result?.toChatCompletionResponse(sessionId, payloads) ?: Result.badRequest(message)).toJSONString(objectMapper)
            )
        }
    }

    override suspend fun embedding(payloads: EmbeddingApiRequest): Any {
        val options = this.buildEmbeddingOptions(payloads.model)
        val messages = listOf(
            PromptMessage.Builder()
                .fromUser()
                .addMessage(PromptMessage.Content.fromString(payloads.input))
                .build()
        )

        val (result, message) = this.doEmbedding(options, messages)
        return if (result != null) {
            EmbeddingApiResponse(
                "list",
                result.results.mapIndexed { index, it ->
                    EmbeddingApiResponse.EmbeddingData(
                        `object` = "embedding",
                        index = index,
                        embedding = it
                    )
                },
                payloads.model,
                EmbeddingApiResponse.Usage(
                    totalTokens = result.totalTokens,
                    promptTokens = result.promptTokens
                )
            )
        } else {
            Result.badRequest(message)
        }
    }

    /**
     * Actual implementation of chat completion
     *
     * @param options [AbstractChatOptions]
     * @param messages List of [PromptMessage]
     * @return
     */
    abstract suspend fun doChatCompletion(options: AbstractChatOptions, messages: List<PromptMessage>): Triple<List<ChatResponseMessage>?, String, String>

    /**
     * Actual implementation of stream chat completion
     *
     * @param options [AbstractChatOptions]
     * @param messages List of [PromptMessage]
     * @return
     */
    abstract fun doStreamChatCompletion(options: AbstractChatOptions, messages: List<PromptMessage>): Flux<*>

    /**
     * Actual implementation of embedding
     *
     * @param options [AbstractEmbeddingOptions]
     * @param messages List of [PromptMessage]
     * @return
     */
    abstract suspend fun doEmbedding(options: AbstractEmbeddingOptions, messages: List<PromptMessage>): Pair<EmbeddingResponseMessage?, String>

    private fun buildChatOptions(model: String): AbstractChatOptions {
        val (platformName, modelName) = model.split("@")

        val builder = chatOptionsBuilders.find {
            it.getPlatformName().lowercase() == platformName.lowercase()
        }

        return builder?.build(modelName) ?: throw PlatformNotSupportException(platformName)
    }

    private fun buildEmbeddingOptions(model: String): AbstractEmbeddingOptions {
        val (platformName, modelName) = model.split("@")

        val builder = embeddingOptionsBuilders.find {
            it.getPlatformName().lowercase() == platformName.lowercase()
        }

        return builder?.build(modelName) ?: throw PlatformNotSupportException(platformName)
    }

    private fun List<ChatResponseMessage>.toChatCompletionResponse(id: String, payloads: ChatCompletionApiRequest): ChatCompletionResponse {
        val totalTokens = this.sumOf { it.totalTokens }
        val completionTokens = this.sumOf { it.generatedTokens }

        return ChatCompletionResponse(
            id = id,
            created = System.currentTimeMillis() / 1000,
            model = payloads.model,
            choices = this.mapIndexed { index, it ->
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
}