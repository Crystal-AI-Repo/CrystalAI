package com.lovelycatv.ai.crystal.dispatcher.data

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-28 17:11
 * @version 1.0
 */
data class EmbeddingApiResponse @JSONCreator constructor(
    @JsonProperty("object")
    val `object`: String,
    @JsonProperty("data")
    val data: List<EmbeddingData>,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("usage")
    val usage: Usage
) {
    data class EmbeddingData @JSONCreator constructor(
        @JsonProperty("object")
        val `object`: String,
        @JsonProperty("index")
        val index: Int,
        @JsonProperty("embedding")
        val embedding: DoubleArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as EmbeddingData

            if (`object` != other.`object`) return false
            if (index != other.index) return false
            return embedding.contentEquals(other.embedding)
        }

        override fun hashCode(): Int {
            var result = `object`.hashCode()
            result = 31 * result + index
            result = 31 * result + embedding.contentHashCode()
            return result
        }
    }

    data class Usage @JsonCreator constructor(
        @JsonProperty("total_tokens")
        val totalTokens: Long,
        @JsonProperty("prompt_tokens")
        val promptTokens: Long
    )
}