package com.lovelycatv.crystal.rag.controller

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.catchException
import com.lovelycatv.crystal.rag.data.VectorDocument
import com.lovelycatv.crystal.rag.data.VectorDocumentSimilarQuery
import com.lovelycatv.crystal.rag.dto.AddVectorDocumentDTO
import com.lovelycatv.crystal.rag.data.VectorDocumentQuery
import com.lovelycatv.crystal.rag.manager.KnowledgeBaseRepositoryManager
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author lovelycat
 * @since 2025-03-26 18:00
 * @version 1.0
 */
@RestController
@RequestMapping("/knowledgeBase")
class KnowledgeBaseController(
    private val knowledgeBaseRepositoryManager: KnowledgeBaseRepositoryManager
) {
    @PostMapping("/add")
    fun addDocument(@RequestBody dto: AddVectorDocumentDTO): Result<*> {
        if (dto.content.isBlank()) {
            return Result.badRequest("Content could not be null or empty")
        }

        val repo = knowledgeBaseRepositoryManager.getRepository(dto.baseName)
            ?: return Result.badRequest("Knowledge base: ${dto.baseName} does not exist")

        if (dto.id != null) {
            repo.remove(dto.id)
        }

        return catchException {
            if (repo.add(
                    VectorDocument(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        content = dto.content,
                        metadata = dto.metadata
                    )
                )) {
                Result.success("Document added successfully")
            } else {
                Result.badRequest("Document add failed")
            }
        }
    }

    @Async
    @PostMapping("/similarQuery")
    fun similarQuery(@RequestBody query: VectorDocumentSimilarQuery): Result<*> {
        if (query.queryContent.isBlank()) {
            return Result.badRequest("Query content could not be null or empty")
        }

        if (query.topK <= 0) {
            return Result.badRequest("TopK should be great than or equals 1")
        }

        val repo = knowledgeBaseRepositoryManager.getRepository(query.baseName)
            ?: return Result.badRequest("Knowledge base: ${query.baseName} does not exist")

        return catchException {
            val result = repo.similaritySearch(VectorDocumentSimilarQuery(
                baseName = query.baseName,
                queryContent = query.queryContent,
                topK = query.topK,
                similarityThreshold = query.similarityThreshold,
                queryConditions = query.queryConditions
            ))

            Result.success("${result.size} result(s) in total", result)
        }
    }

    @Async
    @PostMapping("/query")
    fun query(@RequestBody dto: VectorDocumentQuery): Result<*> {
        val repo = knowledgeBaseRepositoryManager.getRepository(dto.baseName)
            ?: return Result.badRequest("Knowledge base: ${dto.baseName} does not exist")

        return catchException {
            val result = repo.search(dto.conditions)

            Result.success("", result)
        }
    }
}