package com.lovelycatv.crystal.rag.data

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition

data class VectorDocumentQuery @JSONCreator constructor(
    @JsonProperty("baseName")
    val baseName: String,
    @JsonProperty("conditions")
    val conditions: List<AbstractQueryCondition>
)
