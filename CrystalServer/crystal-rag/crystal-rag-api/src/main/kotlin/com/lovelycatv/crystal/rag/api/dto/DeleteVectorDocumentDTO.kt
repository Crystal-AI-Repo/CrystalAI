package com.lovelycatv.crystal.rag.api.dto

/**
 * @author lovelycat
 * @since 2025-04-04 16:00
 * @version 1.0
 */
data class DeleteVectorDocumentDTO(
    val baseName: String,
    val ids: List<String>
)