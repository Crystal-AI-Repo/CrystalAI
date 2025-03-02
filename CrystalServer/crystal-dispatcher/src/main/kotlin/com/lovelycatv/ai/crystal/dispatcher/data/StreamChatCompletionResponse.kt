package com.lovelycatv.ai.crystal.dispatcher.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-03 02:02
 * @version 1.0
 */
data class StreamChatCompletionResponse @JsonCreator constructor(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("created")
    val created: Long? = null,
    @JsonProperty("model")
    val model: String? = null,
    @JsonProperty("choices")
    val choices: List<Choice>,
    @JsonProperty("usage")
    val usage: Usage? = null
) {
    data class Choice @JsonCreator constructor(
        @JsonProperty("index")
        val index: Int,
        @JsonProperty("delta")
        val delta: Delta,
        @JsonProperty("finish_reason")
        val finishReason: String? = null
    ) {
        data class Delta @JsonCreator constructor(
            @JsonProperty("content")
            val content: String
        )
    }

    data class Usage @JsonCreator constructor(
        @JsonProperty("total_tokens")
        val totalTokens: Long,
        @JsonProperty("prompt_tokens")
        val promptTokens: Long,
        @JsonProperty("completion_tokens")
        val completionTokens: Long
    )
}