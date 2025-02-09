package com.lovelycatv.ai.crystal.common.response.actuator

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-02-07 22:49
 * @version 1.0
 */
data class NodeHealthResponse @JsonCreator constructor(
    @JsonProperty("status")
    val status: String
) {
    fun isUp() = status.equals("up", true)
}