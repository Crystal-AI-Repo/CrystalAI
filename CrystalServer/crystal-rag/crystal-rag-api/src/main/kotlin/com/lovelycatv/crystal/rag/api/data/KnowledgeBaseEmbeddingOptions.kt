package com.lovelycatv.crystal.rag.api.data

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModelType

data class KnowledgeBaseEmbeddingOptions @JSONCreator constructor(
    @JsonProperty("type")
    val type: EmbeddingModelType,
    @JsonProperty("modelName")
    val modelName: String,
    @JsonProperty("host")
    val host: String?,
    @JsonProperty("port")
    val port: Int?,
    @JsonProperty("apiKey")
    val apiKey: String?
)