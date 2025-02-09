package com.lovelycatv.ai.crystal.common.response.ollama

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-02-09 21:43
 * @version 1.0
 */
data class OllamaModelMeta @JsonCreator constructor(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("modified_at")
    val modifiedAt: String,
    @JsonProperty("size")
    val size: Long,
    @JsonProperty("digest")
    val digest: String,
    @JsonProperty("details")
    val details: Details
) {
    data class Details @JsonCreator constructor(
        @JsonProperty("parent_model")
        val parentModel: String,
        @JsonProperty("format")
        val format: String,
        @JsonProperty("family")
        val family: String,
        @JsonProperty("families")
        val families: List<String>,
        @JsonProperty("parameter_size")
        val parameterSize: String,
        @JsonProperty("quantization_level")
        val quantizationLevel: String
    )
}