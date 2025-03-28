package com.lovelycatv.crystal.rag.api.embedding

/**
 * @author lovelycat
 * @since 2025-03-26 15:21
 * @version 1.0
 */
interface EmbeddingModel {
    fun embed(content: String): FloatArray

    fun batchEmbed(contentList: List<String>): List<FloatArray> {
        return contentList.map { this.embed(it) }
    }
}