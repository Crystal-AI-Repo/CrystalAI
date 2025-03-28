package com.lovelycatv.crystal.rag.controller

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.catchException
import com.lovelycatv.crystal.rag.data.VectorDocument
import com.lovelycatv.crystal.rag.data.VectorDocumentQuery
import com.lovelycatv.crystal.rag.dto.AddVectorDocumentDTO
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
    @GetMapping("/query")
    fun query(
        baseName: String,
        query: String,
        @RequestParam("topK", required = false, defaultValue = "4")
        topK: Int,
        @RequestParam("similarityThreshold", required = false, defaultValue = "0.0")
        similarityThreshold: Double
    ): Result<*> {
        if (query.isBlank()) {
            return Result.badRequest("Query content could not be null or empty")
        }

        if (topK <= 0) {
            return Result.badRequest("TopK should be great than or equals 1")
        }

        val repo = knowledgeBaseRepositoryManager.getRepository(baseName)
            ?: return Result.badRequest("Knowledge base: $baseName does not exist")

        return catchException {
            val result = repo.similaritySearch(VectorDocumentQuery(
                queryContent = query,
                topK = topK,
                similarityThreshold = similarityThreshold.toFloat()
            ))

            Result.success("${result.size} result(s) in total", result)
        }
    }
}