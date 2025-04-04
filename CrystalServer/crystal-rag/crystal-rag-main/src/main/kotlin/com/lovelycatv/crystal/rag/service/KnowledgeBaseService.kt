package com.lovelycatv.crystal.rag.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.crystal.rag.api.data.KnowledgeBaseEmbeddingOptions
import com.lovelycatv.crystal.rag.entity.KnowledgeBase
import com.lovelycatv.crystal.rag.api.enums.SimilarityFunction
import com.lovelycatv.crystal.rag.mapper.KnowledgeBaseMapper

/**
 * @author lovelycat
 * @since 2025-03-26 17:48
 * @version 1.0
 */
interface KnowledgeBaseService : IService<KnowledgeBase?> {
    fun getMapper(): KnowledgeBaseMapper

    /**
     * Register a new knowledge base.
     * If the knowledge base exists, then return true.
     *
     * @param baseName Knowledge base name
     * @param repositoryType Knowledge base repository type
     * @param dimensions dimensions of Embedding Vector
     * @param similarity Similarity Function
     * @param embeddingOptions [KnowledgeBaseEmbeddingOptions]
     * @param metadata The metadata of the knowledge base
     * @return true if the knowledge base registered
     */
    fun registerKnowledgeBase(
        baseName: String,
        repositoryType: KnowledgeBase.RepositoryType,
        dimensions: Int,
        similarity: SimilarityFunction,
        embeddingOptions: KnowledgeBaseEmbeddingOptions,
        metadata: Any?
    ): Boolean

    /**
     * Once a knowledge base has been registered,
     * only the metadata could be changed
     *
     * @param baseName Knowledge base name
     * @param metadata New metadata of the knowledge base
     * @return true if the knowledge base updated
     */
    fun updateKnowledgeBase(
        baseName: String,
        metadata: Any?
    ): Boolean

    fun getByBaseName(baseName: String): KnowledgeBase?

    fun isKnowledgeBaseExists(baseName: String): Boolean
}