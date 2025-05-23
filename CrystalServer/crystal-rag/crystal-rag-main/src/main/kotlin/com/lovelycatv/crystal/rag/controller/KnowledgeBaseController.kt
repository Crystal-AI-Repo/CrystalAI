package com.lovelycatv.crystal.rag.controller

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.catchException
import com.lovelycatv.crystal.rag.api.data.VectorDocument
import com.lovelycatv.crystal.rag.api.dto.VectorDocumentSimilarQueryDTO
import com.lovelycatv.crystal.rag.api.dto.AddVectorDocumentDTO
import com.lovelycatv.crystal.rag.api.dto.VectorDocumentQueryDTO
import com.lovelycatv.crystal.rag.api.dto.DeleteVectorDocumentDTO
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
            repo.remove(dto.id!!)
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

    @PostMapping("/remove")
    fun removeDocuments(@RequestBody dto: DeleteVectorDocumentDTO): Result<*> {
        if (dto.ids.isEmpty()) {
            return Result.badRequest("Ids could not be null or empty")
        }

        val repo = knowledgeBaseRepositoryManager.getRepository(dto.baseName)
            ?: return Result.badRequest("Knowledge base: ${dto.baseName} does not exist")

        return if (repo.remove(dto.ids))
            Result.success("Documents deleted successfully")
        else
            Result.badRequest("Could not remove documents")
    }

    @Async
    @PostMapping("/similarQuery")
    fun similarQuery(@RequestBody query: VectorDocumentSimilarQueryDTO): Result<*> {
        if (query.queryContent.isBlank()) {
            return Result.badRequest("Query content could not be null or empty")
        }

        if (query.topK <= 0) {
            return Result.badRequest("TopK should be great than or equals 1")
        }

        val repo = knowledgeBaseRepositoryManager.getRepository(query.baseName)
            ?: return Result.badRequest("Knowledge base: ${query.baseName} does not exist")

        return catchException {
            val result = repo.similaritySearch(VectorDocumentSimilarQueryDTO(
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
    fun query(@RequestBody dto: VectorDocumentQueryDTO): Result<*> {
        val repo = knowledgeBaseRepositoryManager.getRepository(dto.baseName)
            ?: return Result.badRequest("Knowledge base: ${dto.baseName} does not exist")

        return catchException {
            val result = repo.search(dto.conditions)

            Result.success("", result)
        }
    }
}