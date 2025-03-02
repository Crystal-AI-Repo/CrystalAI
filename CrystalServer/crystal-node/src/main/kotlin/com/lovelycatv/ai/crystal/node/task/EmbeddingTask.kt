package com.lovelycatv.ai.crystal.node.task

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-03-01 16:39
 * @version 1.0
 */
class EmbeddingTask<EMBEDDING_OPTIONS: AbstractEmbeddingOptions>(
    originalMessageChain: MessageChain,
    expireTime: Long,
    priority: Int,
    val embeddingOptionsClazz: KClass<EMBEDDING_OPTIONS>
) : AbstractTask(Type.EMBEDDING, originalMessageChain, expireTime, priority) {
    @get:JsonIgnore
    @Suppress("UNCHECKED_CAST")
    val chatOptions: EMBEDDING_OPTIONS? get() = this.originalMessageChain.messages.firstOrNull { embeddingOptionsClazz.isInstance(it) } as EMBEDDING_OPTIONS?
}

fun MessageChain.toOllamaEmbeddingTask(expireTime: Long, priority: Int = 0): EmbeddingTask<OllamaEmbeddingOptions> {
    return EmbeddingTask(
        originalMessageChain = this,
        expireTime = expireTime,
        priority = priority,
        embeddingOptionsClazz = OllamaEmbeddingOptions::class
    )
}