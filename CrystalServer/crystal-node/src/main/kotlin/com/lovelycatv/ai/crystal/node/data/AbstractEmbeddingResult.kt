package com.lovelycatv.ai.crystal.node.data

/**
 * @author lovelycat
 * @since 2025-03-02 17:58
 * @version 1.0
 */
abstract class AbstractEmbeddingResult(
    val metadata: Metadata,
    val results: List<DoubleArray>
) {
    data class Metadata(
        val promptTokens: Long,
        val totalTokens: Long
    )
}