package com.lovelycatv.ai.crystal.common.plugin

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.crystal.plugin.data.PluginMetadata

/**
 * @author lovelycat
 * @since 2025-03-23 19:18
 * @version 1.0
 */
class CrystalPluginMetadata @JsonCreator constructor(
    @JsonProperty("name")
    name: String,
    @JsonProperty("description")
    description: String = "",
    @JsonProperty("authors")
    authors: List<String>,
    @JsonProperty("version")
    version: String,
    @JsonProperty("package")
    packageName: String,
    @JsonProperty("main")
    main: String,
    @JsonProperty("nodeMain")
    val nodeMain: String,
    @JsonProperty("nodePackageName")
    val nodePackageName: String,
    @JsonProperty("commonPackageName")
    val commonPackageName: String
) : PluginMetadata(name, description, authors, version, packageName, main)