package com.lovelycatv.crystal.rag.api.embedding.impl

import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModel

/**
 * @author lovelycat
 * @since 2025-03-27 23:14
 * @version 1.0
 */
class CrystalNodeEmbeddingModel(
    host: String,
    port: Int,
    model: String
) : EmbeddingModel {
    override fun embed(content: String): FloatArray {
        TODO("Not yet implemented")
    }
}