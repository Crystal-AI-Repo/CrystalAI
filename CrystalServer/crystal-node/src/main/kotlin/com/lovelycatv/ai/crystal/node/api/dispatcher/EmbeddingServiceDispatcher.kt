package com.lovelycatv.ai.crystal.node.api.dispatcher

import com.lovelycatv.ai.crystal.node.data.AbstractEmbeddingResult
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.node.service.embedding.base.AbstractEmbeddingService
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-03-02 17:47
 * @version 1.0
 */
fun interface EmbeddingServiceDispatcher {
    fun getService(options: KClass<out AbstractEmbeddingOptions>): AbstractEmbeddingService<AbstractEmbeddingOptions, AbstractEmbeddingResult>?
}