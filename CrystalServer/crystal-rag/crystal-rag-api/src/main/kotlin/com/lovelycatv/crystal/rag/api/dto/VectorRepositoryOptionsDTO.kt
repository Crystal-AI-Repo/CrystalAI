package com.lovelycatv.crystal.rag.api.dto

import com.lovelycatv.crystal.rag.api.enums.SimilarityFunction

/**
 * @author lovelycat
 * @since 2025-03-26 15:36
 * @version 1.0
 */
data class VectorRepositoryOptionsDTO(
    val repositoryName: String,
    val similarity: SimilarityFunction,
    val dimensions: Int
)