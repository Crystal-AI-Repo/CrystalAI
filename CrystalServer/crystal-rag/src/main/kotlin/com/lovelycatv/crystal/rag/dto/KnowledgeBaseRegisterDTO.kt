package com.lovelycatv.crystal.rag.dto

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.crystal.rag.entity.KnowledgeBase

/**
 * @author lovelycat
 * @since 2025-03-26 19:24
 * @version 1.0
 */
data class KnowledgeBaseRegisterDTO @JSONCreator constructor(
    @JsonProperty("baseName")
    val baseName: String,
    @JsonProperty("repositoryType")
    val repositoryType: String,
    @JsonProperty("dimensions")
    val dimensions: Int,
    @JsonProperty("similarity")
    val similarity: String,
    @JsonProperty("embeddingOptions")
    val embeddingOptions: KnowledgeBase.EmbeddingOptions,
    @JsonProperty("metadata")
    val metadata: Map<String, Any?>
)