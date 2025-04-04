package com.lovelycatv.crystal.rag.api.rpc

import com.lovelycatv.ai.crystal.common.client.IFeignClient
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.crystal.rag.api.data.VectorDocument
import com.lovelycatv.crystal.rag.api.dto.VectorDocumentQueryDTO
import com.lovelycatv.crystal.rag.api.dto.VectorDocumentSimilarQueryDTO
import com.lovelycatv.crystal.rag.api.dto.AddVectorDocumentDTO
import com.lovelycatv.crystal.rag.api.dto.DeleteVectorDocumentDTO
import com.lovelycatv.crystal.rag.api.dto.KnowledgeBaseRegisterDTO
import com.lovelycatv.crystal.rag.api.dto.KnowledgeBaseUpdateDTO
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * @author lovelycat
 * @since 2025-04-04 16:40
 * @version 1.0
 */
interface CrystalRAGClient : IFeignClient {
    @PostMapping("/knowledgeBase/manager/register")
    fun registerKnowledgeBase(@RequestBody dto: KnowledgeBaseRegisterDTO): Result<Any?>

    @PostMapping("/knowledgeBase/manager/update")
    fun updateKnowledgeBase(@RequestBody dto: KnowledgeBaseUpdateDTO): Result<Any?>

    @PostMapping("/knowledgeBase/add")
    fun addDocument(@RequestBody dto: AddVectorDocumentDTO): Result<Any?>

    @PostMapping("/knowledgeBase/add")
    fun removeDocuments(@RequestBody dto: DeleteVectorDocumentDTO): Result<Any?>

    @PostMapping("/knowledgeBase/similarQuery")
    fun similarQuery(@RequestBody query: VectorDocumentSimilarQueryDTO): Result<List<VectorDocument>>

    @PostMapping("/knowledgeBase/query")
    fun query(@RequestBody query: VectorDocumentQueryDTO): Result<List<VectorDocument>>

}