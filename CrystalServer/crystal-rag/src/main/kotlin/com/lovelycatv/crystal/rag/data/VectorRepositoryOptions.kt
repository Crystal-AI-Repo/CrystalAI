package com.lovelycatv.crystal.rag.data

import com.lovelycatv.crystal.rag.enums.SimilarityFunction

/**
 * @author lovelycat
 * @since 2025-03-26 15:36
 * @version 1.0
 */
data class VectorRepositoryOptions(
    val repositoryName: String,
    val similarity: SimilarityFunction,
    val dimensions: Int
)