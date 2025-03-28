package com.lovelycatv.ai.crystal.openapi.dto

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-28 17:09
 * @version 1.0
 */
data class EmbeddingApiRequest @JSONCreator constructor(
    @JsonProperty("input")
    val input: List<String>,
    @JsonProperty("model")
    val model: String
)