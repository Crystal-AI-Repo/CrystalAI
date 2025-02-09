package com.lovelycatv.ai.crystal.common.response.dispatcher

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class NodeRegisterResult @JsonCreator constructor(
    @JsonProperty("success")
    val success: Boolean,
    @JsonProperty("message")
    val message: String = "",
    @JsonProperty("uuid")
    val uuid: String? = null
)
