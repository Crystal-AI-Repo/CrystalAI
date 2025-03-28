package com.lovelycatv.crystal.rag.data

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-25 14:38
 * @version 1.0
 */
data class VectorDocumentQuery @JSONCreator constructor(
    @JsonProperty("queryContent")
    val queryContent: String,
    @JsonProperty("topK")
    val topK: Int,
    @JsonProperty("similarityThreshold")
    val similarityThreshold: Float = 0f
)