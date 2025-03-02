package com.lovelycatv.ai.crystal.node.plugin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lovelycat
 * @since 2025-03-02 15:07
 * @version 1.0
 */
data class PluginMetadata @JsonCreator constructor(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String = "",
    @JsonProperty("authors")
    val authors: List<String>,
    @JsonProperty("version")
    val version: String,
    @JsonProperty("package")
    val packageName: String,
    @JsonProperty("main")
    val main: String
)