package com.lovelycatv.crystal.openapi.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-03 01:33
 * @version 1.0
 */
data class ChatCompletionMessage @JsonCreator constructor(
    @JsonProperty("role")
    val role: String,
    @JsonProperty("content")
    val content: String
)