package com.lovelycatv.ai.crystal.common.response.deepseek

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-02-28 18:10
 * @version 1.0
 */
data class DeepSeekModelResults @JsonCreator constructor(
    @JsonProperty("object")
    val `object`: String,
    @JsonProperty("data")
    val data: List<DeepSeekModelMeta>,
) {
    data class DeepSeekModelMeta @JsonCreator constructor(
        @JsonProperty("id")
        val id: String,
        @JsonProperty("owned_by")
        val ownedBy: String,
        @JsonProperty("object")
        val `object`: String
    )
}