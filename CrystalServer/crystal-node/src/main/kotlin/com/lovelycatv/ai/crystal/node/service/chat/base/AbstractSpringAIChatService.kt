package com.lovelycatv.ai.crystal.node.service.chat.base

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.util.divide
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.node.api.interfaces.model.ChatOptions2SpringAIOptionsTranslator
import com.lovelycatv.ai.crystal.node.data.AbstractChatResult
import com.lovelycatv.ai.crystal.node.data.PackagedChatServiceResult
import com.lovelycatv.ai.crystal.node.data.SpringAIChatResult
import com.lovelycatv.ai.crystal.node.exception.UnsupportedMessageContentType
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.Media
import org.springframework.ai.retry.NonTransientAiException
import org.springframework.ai.retry.TransientAiException
import org.springframework.core.io.Resource
import reactor.core.publisher.Flux
import java.net.URL

/**
 * @author lovelycat
 * @since 2025-02-28 15:08
 * @version 1.0
 */
abstract class AbstractSpringAIChatService<CHAT_MODEL: ChatModel, MODEL_OPTIONS: ChatOptions, OPTIONS: AbstractChatOptions>(
    private var defaultChatModel: CHAT_MODEL? = null,
    private val translatorDelegate: ChatOptions2SpringAIOptionsTranslator<OPTIONS, MODEL_OPTIONS>
) : AbstractChatService<OPTIONS, SpringAIChatResult, Flux<ChatResponse>>(),
    ChatOptions2SpringAIOptionsTranslator<OPTIONS, MODEL_OPTIONS> by translatorDelegate
{
    private val logger = logger()

    abstract fun buildChatModel(): CHAT_MODEL

    override fun streamGenerateImpl(
        stream: Flux<ChatResponse>,
        onNewTokenReceived: ChatStreamCallback?,
        onCompleted: ChatStreamCompletedCallback
    ) {
        val receivedMessage = mutableListOf<String>()

        var generatedTokens = 0L
        var totalTokens = 0L

        stream.doOnComplete {
            onCompleted.invoke(receivedMessage, generatedTokens, totalTokens)
        }.subscribe {
            totalTokens = it.metadata.usage.totalTokens
            generatedTokens = it.metadata.usage.generationTokens

            val generatedText = it.result.output.text
            receivedMessage.add(generatedText)
            onNewTokenReceived?.invoke(generatedText)
        }
    }

    override suspend fun generate(content: List<PromptMessage>, options: OPTIONS?, stream: Boolean): PackagedChatServiceResult<Any?> {
        if (this.defaultChatModel == null) {
            this.defaultChatModel = buildChatModel()
        }

        val builtPrompt = content.map { promptMessage ->
            val (textList, mediaList) = promptMessage.message.divide { it.type == PromptMessage.Content.Type.TEXT }

            val plainText = textList.joinToString(separator = promptMessage.messageSeparator) { it.stringContent() }
            val medias = mediaList.map {
                Media.builder().apply {
                    when (it.content) {
                        is Resource -> this.data(it.content).build()
                        is URL -> this.data(it.content).build()
                        else -> throw UnsupportedMessageContentType(it.content::class.qualifiedName ?: "Unknown Class Name")
                    }
                }.build()
            }

            when (promptMessage.role) {
                PromptMessage.Role.USER -> {
                    UserMessage(plainText, medias)
                }
                PromptMessage.Role.ASSISTANT -> {
                    AssistantMessage(plainText, mapOf(), listOf(), medias)
                }
                PromptMessage.Role.SYSTEM -> {
                    SystemMessage(plainText)
                }
            }
        }

        val builtOptions = if (options != null) translate(options) else null

        val prompt = if (builtOptions != null) {
            Prompt(builtPrompt, builtOptions)
        } else {
            // Using default model options specified by this.buildChatModel()
            Prompt(builtPrompt)
        }

        return try {
            if (stream)
                PackagedChatServiceResult.success(this.defaultChatModel!!.stream(prompt))
            else
                PackagedChatServiceResult.success(this.defaultChatModel!!.call(prompt).run {
                    val response = this
                    // Transform to AbstractChatResult
                    SpringAIChatResult(
                        metadata = AbstractChatResult.Metadata(
                            generatedTokens = response.metadata.usage.generationTokens,
                            totalTokens = response.metadata.usage.totalTokens
                        ),
                        results = this.results.map {
                            AbstractChatResult.Result(
                                content = it.output.content
                            )
                        }
                    )
                })
        } catch (e: NonTransientAiException) {
            logger.warn("Could not send chat request, message: [${e.message}], options: ${options.toJSONString()}, stream: $stream", e)
            PackagedChatServiceResult.failed("Request failed, message: " + e.message, e)
        } catch (e: TransientAiException) {
            logger.warn("Could not send chat request, message: [${e.message}], options: ${options.toJSONString()}, stream: $stream", e)
            PackagedChatServiceResult.failed("Request failed, message: " + e.message, e)
        }

    }
}