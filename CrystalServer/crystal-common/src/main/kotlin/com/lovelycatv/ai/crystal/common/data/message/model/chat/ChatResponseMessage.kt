package com.lovelycatv.ai.crystal.common.data.message.model.chat

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage

/**
 * @author lovelycat
 * @since 2025-02-26 21:23
 * @version 1.0
 */
@JsonTypeName("CHAT_RESPONSE")
data class ChatResponseMessage @JsonCreator constructor(
    @JsonProperty("success")
    val success: Boolean,
    @JsonProperty("message")
    val message: String?,
    @JsonProperty("content")
    val content: String? = null,
    @JsonProperty("generatedTokens")
    val generatedTokens: Long = 0L,
    @JsonProperty("totalTokens")
    val totalTokens: Long = 0L
) : AbstractMessage(Type.CHAT_RESPONSE) {
    companion object {
        fun failed(message: String): ChatResponseMessage {
            return ChatResponseMessage(success = false, message = message)
        }
    }

    @JsonIgnore
    fun isFinished() = GlobalConstants.Flags.isFinishedFlag(this.message)
}