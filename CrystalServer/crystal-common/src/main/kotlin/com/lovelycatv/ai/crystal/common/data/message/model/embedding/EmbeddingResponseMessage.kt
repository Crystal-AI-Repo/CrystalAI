package com.lovelycatv.ai.crystal.common.data.message.model.embedding

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage

/**
 * @author lovelycat
 * @since 2025-03-01 16:34
 * @version 1.0
 */
@JsonTypeName("EMBEDDING_RESPONSE")
class EmbeddingResponseMessage @JsonCreator constructor(
    success: Boolean,
    message: String?,
    @JsonProperty("results")
    val results: List<DoubleArray> = listOf(),
    @JsonProperty("promptTokens")
    val promptTokens: Long = 0L,
    @JsonProperty("totalTokens")
    val totalTokens: Long = 0L
) : ModelResponseMessage(success, message, Type.EMBEDDING_RESPONSE) {
    companion object {
        fun success(message: String?, results: List<DoubleArray>, promptTokens: Long, totalTokens: Long): EmbeddingResponseMessage {
            return EmbeddingResponseMessage(true, message, results, promptTokens, totalTokens)
        }

        fun failed(message: String): EmbeddingResponseMessage {
            return ModelResponseMessage.failed(message)
        }
    }
}