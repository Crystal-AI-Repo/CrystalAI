package com.lovelycatv.ai.crystal.node.service.embedding.base

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.node.data.AbstractEmbeddingResult

/**
 * @author lovelycat
 * @since 2025-03-01 14:53
 * @version 1.0
 */
abstract class AbstractEmbeddingService<OPTIONS: AbstractEmbeddingOptions, RESULT: AbstractEmbeddingResult> {

    abstract suspend fun embedding(
        options: OPTIONS?,
        content: List<PromptMessage>
    ): RESULT
}