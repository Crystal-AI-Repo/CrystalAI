package com.lovelycatv.ai.crystal.common.data.message.model.chat

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage

/**
 * @author lovelycat
 * @since 2025-02-26 21:23
 * @version 1.0
 */
@JsonTypeName("CHAT_RESPONSE")
class ChatResponseMessage @JsonCreator constructor(
    success: Boolean,
    message: String?,
    @JsonProperty("content")
    val content: String? = null,
    @JsonProperty("generatedTokens")
    val generatedTokens: Long = 0L,
    @JsonProperty("totalTokens")
    val totalTokens: Long = 0L
) : ModelResponseMessage(success, message, Type.CHAT_RESPONSE) {
    companion object {
        fun failed(message: String?): ChatResponseMessage {
            return failed(message, null, 0L, 0L)
        }
    }
}