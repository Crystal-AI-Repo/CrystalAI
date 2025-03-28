package com.lovelycatv.crystal.rag.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.crystal.rag.entity.KnowledgeBase
import com.lovelycatv.crystal.rag.enums.SimilarityFunction
import com.lovelycatv.crystal.rag.mapper.KnowledgeBaseMapper
import com.lovelycatv.crystal.rag.service.KnowledgeBaseService
import org.springframework.stereotype.Service
import java.util.*

/**
 * @author lovelycat
 * @since 2025-03-26 17:48
 * @version 1.0
 */
@Service
class KnowledgeBaseServiceImpl(
    private val objectMapper: ObjectMapper,
    private val knowledgeBaseMapper: KnowledgeBaseMapper
) : KnowledgeBaseService, ServiceImpl<KnowledgeBaseMapper, KnowledgeBase?>() {
    override fun getMapper(): KnowledgeBaseMapper = knowledgeBaseMapper

    /**
     * Register a new knowledge base.
     * If the knowledge base exists, then return true.
     *
     * @param baseName Knowledge base name
     * @param repositoryType Knowledge base repository type
     * @param dimensions dimensions of Embedding Vector
     * @param similarity Similarity Function
     * @param embeddingOptions [KnowledgeBase.EmbeddingOptions]
     * @param metadata The metadata of the knowledge base
     * @return true if the knowledge base registered
     */
    override fun registerKnowledgeBase(
        baseName: String,
        repositoryType: KnowledgeBase.RepositoryType,
        dimensions: Int,
        similarity: SimilarityFunction,
        embeddingOptions: KnowledgeBase.EmbeddingOptions,
        metadata: Any?
    ): Boolean {
        if (this.isKnowledgeBaseExists(baseName)) {
            return true
        }

        if (dimensions <= 0) {
            throw IllegalArgumentException("Dimensions must be greater than 0")
        }

        return save(KnowledgeBase(
            id = UUID.randomUUID().toString(),
            baseName = baseName,
            dimensions = dimensions,
            similarity = similarity.typeNames.firstOrNull() ?: similarity.name,
            repositoryType = repositoryType.typeName,
            embeddingOptions = objectMapper.writeValueAsString(embeddingOptions),
            metadata = if (metadata != null) objectMapper.writeValueAsString(metadata) else ""
        ))
    }

    /**
     * Once a knowledge base has been registered,
     * only the metadata could be changed
     *
     * @param baseName Knowledge base name
     * @param metadata New metadata of the knowledge base
     * @return true if the knowledge base updated
     */
    override fun updateKnowledgeBase(baseName: String, metadata: Any?): Boolean {
        val existingBase = this.getByBaseName(baseName) ?: return false

        return updateById(
            existingBase.copy(
                metadata = if (metadata != null) objectMapper.writeValueAsString(metadata) else ""
            )
        )
    }

    override fun getByBaseName(baseName: String): KnowledgeBase? {
        return getOne(QueryWrapper<KnowledgeBase>().eq("base_name", baseName).last("LIMIT 1"))
    }

    override fun isKnowledgeBaseExists(baseName: String): Boolean {
        return getByBaseName(baseName) != null
    }
}