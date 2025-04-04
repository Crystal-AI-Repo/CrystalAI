package com.lovelycatv.crystal.rag.manager

import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.crystal.rag.api.repository.AbstractVectorRepository
import com.lovelycatv.crystal.rag.repository.ElasticSearchVectorRepository
import com.lovelycatv.crystal.rag.sharding.LengthBasedVectorDocumentShardingStrategy
import com.lovelycatv.crystal.rag.api.dto.VectorRepositoryOptionsDTO
import com.lovelycatv.crystal.rag.entity.KnowledgeBase
import com.lovelycatv.crystal.rag.service.KnowledgeBaseService
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author lovelycat
 * @since 2025-03-26 19:37
 * @version 1.0
 */
@Component
class KnowledgeBaseRepositoryManager(
    private val embeddingWorkerManager: EmbeddingWorkerManager,
    private val knowledgeBaseService: KnowledgeBaseService
) : InitializingBean {
    private val logger = logger()

    private val lock = ReentrantLock()

    private val _repositories = mutableMapOf<String, AbstractVectorRepository>()
    val repositories: Map<String, AbstractVectorRepository> get() = this._repositories

    fun getRepository(baseName: String) = this.repositories[baseName]

    private fun buildRepository(knowledgeBase: KnowledgeBase): AbstractVectorRepository {
        return when (knowledgeBase.getRepositoryEnumType()) {
            KnowledgeBase.RepositoryType.ELASTICSEARCH -> {
                val metadata: KnowledgeBase.ElasticSearchMetadata = knowledgeBase.getMetadata()
                    ?: throw IllegalStateException("Invalid metadata, requires: ${KnowledgeBase.ElasticSearchMetadata::class.qualifiedName}")
                ElasticSearchVectorRepository(
                    metadata.host,
                    metadata.port,
                    metadata.ssl,
                    metadata.username,
                    metadata.password,
                    metadata.initSchema,
                    VectorRepositoryOptionsDTO(
                        repositoryName = knowledgeBase.baseName,
                        dimensions = knowledgeBase.dimensions,
                        similarity = knowledgeBase.getSimilarityEnumType()
                    ),
                    embeddingWorkerManager.getEmbeddingModel(knowledgeBase)
                        ?: throw IllegalStateException("Could not find embedding model for knowledge base: ${knowledgeBase.baseName}, " +
                            "embeddingOptions: ${knowledgeBase.getExplicitEmbeddingOptions().toJSONString()}"),
                    LengthBasedVectorDocumentShardingStrategy((8192 * 0.9).toInt())
                )
            }
        }
    }

    override fun afterPropertiesSet() {
        refreshAllRepository()
    }

    fun refreshAllRepository() {
        lock.withLock {
            logger.info("Refreshing all vector document repositories...")
            this._repositories.clear()
            knowledgeBaseService.list().filterNotNull().forEach {
                this._repositories[it.baseName] = this.buildRepository(it)
            }
            logger.info("${this.repositories.size} repositories detected:")
            this.repositories.forEach { (baseName, repo) ->
                logger.info("> $baseName: ")
                logger.info("    class: ${repo::class.java.canonicalName}")
                logger.info("    options: ${repo.options}")
            }
        }
    }

    fun refreshRepository(baseName: String) {
        lock.withLock {
            val fromDatabase = knowledgeBaseService.getByBaseName(baseName)

            if (fromDatabase != null) {
                if (this.repositories.containsKey(baseName)) {
                    this._repositories[baseName] = this.buildRepository(fromDatabase)
                } else {
                    this._repositories[fromDatabase.baseName] = this.buildRepository(fromDatabase)
                }
            }
        }
    }
}