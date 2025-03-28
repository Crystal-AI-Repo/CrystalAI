package com.lovelycatv.crystal.rag.api.repository

import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModel
import com.lovelycatv.crystal.rag.api.document.VectorDocumentShardingStrategy
import com.lovelycatv.crystal.rag.data.VectorRepositoryOptions

/**
 * @author lovelycat
 * @since 2025-03-26 15:18
 * @version 1.0
 */
abstract class AbstractVectorRepository(
    val options: VectorRepositoryOptions,
    val embeddingModel: EmbeddingModel,
    val documentShardingStrategy: VectorDocumentShardingStrategy
) : VectorRepository