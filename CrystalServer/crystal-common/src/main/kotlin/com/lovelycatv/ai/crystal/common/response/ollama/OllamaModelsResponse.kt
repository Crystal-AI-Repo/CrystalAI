package com.lovelycatv.ai.crystal.common.response.ollama

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-02-09 21:42
 * @version 1.0
 */
data class OllamaModelsResponse @JsonCreator constructor(
    @JsonProperty("models")
    val models: List<OllamaModelMeta>
)