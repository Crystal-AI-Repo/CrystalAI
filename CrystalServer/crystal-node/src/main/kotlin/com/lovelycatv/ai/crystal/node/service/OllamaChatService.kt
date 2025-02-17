package com.lovelycatv.ai.crystal.node.service

import com.lovelycatv.ai.crystal.common.util.divide
import com.lovelycatv.ai.crystal.common.data.message.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.node.exception.UnsupportedMessageType
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.Media
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.core.io.Resource
import reactor.core.publisher.Flux
import java.net.URL

/**
 * @author lovelycat
 * @since 2025-02-15 16:05
 * @version 1.0
 */
abstract class OllamaChatService(
    private var defaultChatModel: OllamaChatModel? = null
) {
    abstract fun buildChatModel(): OllamaChatModel

    @Suppress("UNCHECKED_CAST")
    fun streamGenerate(
        content: List<PromptMessage>,
        options: OllamaChatOptions?,
        onNewTokenReceived: ((String) -> Unit)? = null,
        onCompleted: (received: List<String>, generatedTokens: Long, totalTokens: Long) -> Unit
    ) {
        val stream = this.generate(content, options, true) as Flux<ChatResponse>

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

    fun blockingGenerate(content: List<PromptMessage>, options: OllamaChatOptions?): ChatResponse {
        return this.generate(content, options, false) as ChatResponse
    }

    private fun generate(content: List<PromptMessage>, options: OllamaChatOptions?, stream: Boolean): Any {
        if (this.defaultChatModel == null) {
            this.defaultChatModel = buildChatModel()
        }

        val builtPrompt = content.map {
            val (textList, mediaList) = it.message.divide { it.type == PromptMessage.Content.Type.TEXT }

            val plainText = textList.joinToString(separator = it.messageSeparator)
            val medias = mediaList.map {
                Media.builder().apply {
                    when (it.content) {
                        is Resource -> this.data(it.content).build()
                        is URL -> this.data(it.content).build()
                        else -> throw UnsupportedMessageType(it.content::class.qualifiedName ?: "Unknown Class Name")
                    }
                }.build()
            }

            when (it.role) {
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

        val builtOptions = if (options != null) OllamaOptions.builder().apply {
            options.modelName?.let {
                this.model(it)
            }
            options.temperature?.let {
                this.temperature(it)
            }
        }.build() else null

        val prompt = if (builtOptions != null) {
            Prompt(builtPrompt, builtOptions)
        } else {
            // Using default model options specified by this.buildChatModel()
            Prompt(builtPrompt)
        }

        return if (stream)
            this.defaultChatModel!!.stream(prompt)
        else
            this.defaultChatModel!!.call(prompt)
    }
}