package com.lovelycatv.crystal.rag.api.repository

import com.lovelycatv.crystal.rag.data.VectorDocument
import com.lovelycatv.crystal.rag.data.VectorDocumentSimilarQuery
import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition

/**
 * @author lovelycat
 * @since 2025-03-26 14:59
 * @version 1.0
 */
interface VectorRepository {
    fun getRepositoryName(): String

    fun add(vararg documents: VectorDocument): Boolean

    fun remove(vararg documentIds: String): Boolean

    fun remove(documentIds: List<String>): Boolean {
        return this.remove(*documentIds.toTypedArray())
    }

    fun similaritySearch(query: VectorDocumentSimilarQuery): List<VectorDocument>

    fun search(queryConditions: List<AbstractQueryCondition>): List<VectorDocument>

    fun isRepositoryExists(): Boolean
}