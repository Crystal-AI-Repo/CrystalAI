package com.lovelycatv.ai.crystal.node.data

/**
 * @author lovelycat
 * @since 2025-03-02 18:23
 * @version 1.0
 */
abstract class AbstractChatResult(
    val metadata: Metadata,
    val results: List<Result>
) {
    data class Result(
        val content: String
    )

    data class Metadata(
        val generatedTokens: Long,
        val totalTokens: Long
    )
}