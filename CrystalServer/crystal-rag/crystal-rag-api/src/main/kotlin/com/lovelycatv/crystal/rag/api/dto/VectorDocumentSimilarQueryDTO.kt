package com.lovelycatv.crystal.rag.api.dto

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition

/**
 * @author lovelycat
 * @since 2025-03-25 14:38
 * @version 1.0
 */
data class VectorDocumentSimilarQueryDTO @JSONCreator constructor(
    @JsonProperty("baseName")
    val baseName: String,
    @JsonProperty("queryContent")
    val queryContent: String,
    @JsonProperty("topK")
    val topK: Int = 4,
    @JsonProperty("similarityThreshold")
    val similarityThreshold: Float = 0f,
    @JsonProperty("queryConditions")
    val queryConditions: List<AbstractQueryCondition> = listOf()
)