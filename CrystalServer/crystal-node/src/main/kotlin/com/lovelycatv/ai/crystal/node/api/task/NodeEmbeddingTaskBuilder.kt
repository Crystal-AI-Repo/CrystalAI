package com.lovelycatv.ai.crystal.node.api.task

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.node.task.EmbeddingTask

/**
 * @author lovelycat
 * @since 2025-03-03 00:17
 * @version 1.0
 */
interface NodeEmbeddingTaskBuilder<O: AbstractEmbeddingOptions> {
    fun buildEmbeddingTask(originalMessageChain: MessageChain): EmbeddingTask<O>

    fun getOptionsClass(): Class<O>
}