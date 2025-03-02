package com.lovelycatv.ai.crystal.dispatcher.data

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-03 01:31
 * @version 1.0
 */
data class ChatCompletionResponse @JsonCreator constructor(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("created")
    val created: Long,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("choices")
    val choices: List<Choice>,
    @JsonProperty("usage")
    val usage: Usage
) {
    data class Choice @JsonCreator constructor(
        @JsonProperty("index")
        val index: Int,
        @JsonProperty("message")
        val message: ChatCompletionMessage,
        @JsonProperty("finish_reason")
        val finishReason: String
    )

    data class Usage @JsonCreator constructor(
        @JsonProperty("total_tokens")
        val totalTokens: Long,
        @JsonProperty("prompt_tokens")
        val promptTokens: Long,
        @JsonProperty("completion_tokens")
        val completionTokens: Long
    )
}