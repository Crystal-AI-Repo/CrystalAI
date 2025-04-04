package com.lovelycatv.crystal.rag.api.dto

import com.alibaba.fastjson2.annotation.JSONCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition

data class VectorDocumentQueryDTO @JSONCreator constructor(
    @JsonProperty("baseName")
    val baseName: String,
    @JsonProperty("conditions")
    val conditions: List<AbstractQueryCondition>
)
