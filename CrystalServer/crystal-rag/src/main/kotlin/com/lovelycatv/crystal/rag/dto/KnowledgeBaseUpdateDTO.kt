package com.lovelycatv.crystal.rag.dto

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-26 19:29
 * @version 1.0
 */
data class KnowledgeBaseUpdateDTO @JSONCreator constructor(
    @JsonProperty("baseName")
    val baseName: String,
    @JsonProperty("metadata")
    val metadata: Map<String, Any?>
)