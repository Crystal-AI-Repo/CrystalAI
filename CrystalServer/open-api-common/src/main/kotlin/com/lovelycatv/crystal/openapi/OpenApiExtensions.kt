package com.lovelycatv.crystal.openapi

import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.crystal.openapi.dto.StreamChatCompletionResponse

/**
 * @author lovelycat
 * @since 2025-03-28 18:42
 * @version 1.0
 */
class OpenApiExtensions private constructor()

fun List<ChatResponseMessage>.toStreamChatCompletionResponse(id: String, finally: Boolean): StreamChatCompletionResponse {
    val totalTokens = this.sumOf { it.totalTokens }
    val completionTokens = this.sumOf { it.generatedTokens }

    return StreamChatCompletionResponse(
        id = id,
        choices = this.mapIndexed { index, it ->
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