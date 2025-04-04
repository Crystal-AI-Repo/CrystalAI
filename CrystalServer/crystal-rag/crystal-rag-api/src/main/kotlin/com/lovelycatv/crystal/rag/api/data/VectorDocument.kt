package com.lovelycatv.crystal.rag.api.data

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * @author lovelycat
 * @since 2025-03-26 15:01
 * @version 1.0
 */
data class VectorDocument @JSONCreator constructor(
    @JsonProperty("id")
    val id: String = UUID.randomUUID().toString(),
    /**
     * If true, this document is a piece of full content.
     * To get the full document, turn to "_identity" and "_order" in [metadata]
     */
    @JsonProperty("apart")
    val apart: Boolean = false,
    @JsonProperty("content")
    val content: String,
    @JsonProperty("metadata")
    val metadata: Map<String, Any?> = mapOf(),
    @JsonProperty("score")
    val score: Double = 0.0
) {
    companion object {
        const val DISTANCE = "_distance"
        const val IDENTITY = "_identity"
        const val ORDER = "_order"
    }

    @JsonIgnore
    fun getIdentityOrNull() = this.metadata[IDENTITY] as? String

    @JsonIgnore
    fun getOrderOrNull() = this.metadata[ORDER] as? Int
}