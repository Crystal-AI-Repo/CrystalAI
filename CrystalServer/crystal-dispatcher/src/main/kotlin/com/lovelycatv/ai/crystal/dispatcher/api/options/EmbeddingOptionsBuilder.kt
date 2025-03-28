package com.lovelycatv.ai.crystal.dispatcher.api.options

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions

/**
 * @author lovelycat
 * @since 2025-03-19 23:06
 * @version 1.0
 */
interface EmbeddingOptionsBuilder<O: AbstractEmbeddingOptions> {
    fun build(modelName: String): O

    fun getPlatformName(): String

    fun getOptionsClass(): Class<O>
}