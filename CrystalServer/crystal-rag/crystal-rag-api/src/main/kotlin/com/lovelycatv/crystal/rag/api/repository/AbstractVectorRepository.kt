package com.lovelycatv.crystal.rag.api.repository

import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModel
import com.lovelycatv.crystal.rag.api.sharding.VectorDocumentShardingStrategy
import com.lovelycatv.crystal.rag.api.dto.VectorRepositoryOptionsDTO

/**
 * @author lovelycat
 * @since 2025-03-26 15:18
 * @version 1.0
 */
abstract class AbstractVectorRepository(
    val options: VectorRepositoryOptionsDTO,
    val embeddingModel: EmbeddingModel,
    val documentShardingStrategy: VectorDocumentShardingStrategy
) : VectorRepository