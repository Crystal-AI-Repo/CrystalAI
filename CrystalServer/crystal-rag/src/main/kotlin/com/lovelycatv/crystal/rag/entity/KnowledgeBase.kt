package com.lovelycatv.crystal.rag.entity

import com.alibaba.fastjson2.annotation.JSONCreator
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.util.toExplicitObject
import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModelType
import com.lovelycatv.crystal.rag.enums.SimilarityFunction

/**
 * @author lovelycat
 * @since 2025-03-25 14:23
 * @version 1.0
 */
@TableName("knowledge_bases")
data class KnowledgeBase(
    @TableId
    val id: String,
    @TableField("base_name")
    val baseName: String,
    @TableField("dimensions")
    val dimensions: Int,
    @TableField("similarity")
    val similarity: String,
    @TableField("repository_type")
    val repositoryType: String,
    @TableField("embedding_options")
    val embeddingOptions: String,
    @TableField("metadata")
    val metadata: String
) {
    @JsonIgnore
    fun getRepositoryEnumType() = RepositoryType.fromTypeName(this.repositoryType)!!

    @JsonIgnore
    fun getSimilarityEnumType() = SimilarityFunction.fromTypeName(this.similarity)!!

    @JsonIgnore
    fun getExplicitEmbeddingOptions() = ObjectMapper().readValue(this.embeddingOptions, EmbeddingOptions::class.java)!!

    @JsonIgnore
    inline fun <reified T> getMetadata(): T? {
        return if (this.metadata.isBlank())
            null
        else
            this.metadata.toExplicitObject(ObjectMapper().apply {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            })
    }

    enum class RepositoryType(val typeName: String) {
        ELASTICSEARCH("ElasticSearch");

        companion object {
            fun fromTypeName(typeName: String) = entries.find { it.typeName.lowercase() == typeName.lowercase() }
        }
    }

    data class ElasticSearchMetadata @JSONCreator constructor(
        @JsonProperty("host")
        val host: String,
        @JsonProperty("port")
        val port: Int,
        @JsonProperty("ssl")
        val ssl: Boolean,
        @JsonProperty("username")
        val username: String,
        @JsonProperty("password")
        val password: String,
        @JsonProperty("initSchema")
        val initSchema: Boolean
    )

    data class EmbeddingOptions @JSONCreator constructor(
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
}