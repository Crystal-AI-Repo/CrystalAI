package com.lovelycatv.ai.crystal.common.response.node.probe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.ai.crystal.common.response.deepseek.DeepSeekModelResults
import com.lovelycatv.ai.crystal.common.response.ollama.OllamaModelMeta

/**
 * @author lovelycat
 * @since 2025-02-08 01:54
 * @version 1.0
 */
data class NodeProbeResult @JsonCreator constructor(
    @JsonProperty("nodeName")
    val nodeName: String,
    @JsonProperty("nodeIp")
    val nodeIp: String,
    @JsonProperty("nodePort")
    val nodePort: Int,
    @JsonProperty("ssl")
    val ssl: Boolean,
    @JsonProperty("ollamaModels")
    val ollamaModels: List<OllamaModelMeta>,
    @JsonProperty("deepseekModels")
    val deepseekModels: List<DeepSeekModelResults.DeepSeekModelMeta>
)