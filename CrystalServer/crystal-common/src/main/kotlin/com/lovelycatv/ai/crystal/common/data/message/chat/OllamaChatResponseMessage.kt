package com.lovelycatv.ai.crystal.common.data.message.chat

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage

/**
 * @author lovelycat
 * @since 2025-02-26 21:23
 * @version 1.0
 */
@JsonTypeName("OLLAMA_CHAT_RESPONSE")
data class OllamaChatResponseMessage @JsonCreator constructor(
    @JsonProperty("success")
    val success: Boolean,
    @JsonProperty("message")
    val message: String,
    @JsonProperty("message")
    val content: String? = null,
    @JsonProperty("generatedTokens")
    val generatedTokens: Long = 0L,
    @JsonProperty("totalTokens")
    val totalTokens: Long = 0L
) : AbstractMessage(Type.OLLAMA_CHAT_RESPONSE) {
    companion object {
        fun failed(message: String): OllamaChatResponseMessage {
            return OllamaChatResponseMessage(success = false, message = message)
        }
    }
}