package com.lovelycatv.crystal.rag.dto

/**
 * @author lovelycat
 * @since 2025-04-04 16:00
 * @version 1.0
 */
data class DocumentDeletionDTO(
    val baseName: String,
    val ids: List<String>
)