package com.lovelycatv.crystal.rag.dto

/**
 * @author lovelycat
 * @since 2025-03-26 20:30
 * @version 1.0
 */
data class AddVectorDocumentDTO(
    val baseName: String,
    val id: String?,
    val content: String,
    val metadata: Map<String, Any?>
)