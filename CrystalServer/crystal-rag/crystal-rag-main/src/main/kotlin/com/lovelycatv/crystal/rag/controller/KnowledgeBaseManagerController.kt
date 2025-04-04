package com.lovelycatv.crystal.rag.controller

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.catchException
import com.lovelycatv.crystal.rag.api.dto.KnowledgeBaseRegisterDTO
import com.lovelycatv.crystal.rag.api.dto.KnowledgeBaseUpdateDTO
import com.lovelycatv.crystal.rag.entity.KnowledgeBase
import com.lovelycatv.crystal.rag.api.enums.SimilarityFunction
import com.lovelycatv.crystal.rag.service.KnowledgeBaseService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-03-26 18:00
 * @version 1.0
 */
@RestController
@RequestMapping("/knowledgeBase/manager")
class KnowledgeBaseManagerController(
    private val knowledgeBaseService: KnowledgeBaseService
) {
    @PostMapping("/register")
    fun register(@RequestBody dto: KnowledgeBaseRegisterDTO): Result<*> {
        val type = KnowledgeBase.RepositoryType.fromTypeName(dto.repositoryType)
            ?: return Result.badRequest("Repository Type: ${dto.repositoryType} is not supported")

        val similarity = SimilarityFunction.fromTypeName(dto.similarity)
            ?: return Result.badRequest("Similarity Function Type: ${dto.similarity} is not supported")

        return catchException {
            if (knowledgeBaseService.registerKnowledgeBase(dto.baseName, type, dto.dimensions, similarity, dto.embeddingOptions, dto.metadata)) {
                Result.success("Created")
            } else {
                Result.badRequest("Failed")
            }
        }
    }

    @PostMapping("/update")
    fun update(@RequestBody dto: KnowledgeBaseUpdateDTO): Result<*> {
        return catchException {
            if (knowledgeBaseService.updateKnowledgeBase(dto.baseName,dto.metadata)) {
                Result.success("Updated")
            } else {
                Result.badRequest("Failed")
            }
        }
    }
}