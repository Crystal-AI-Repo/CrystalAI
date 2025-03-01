package com.lovelycatv.ai.crystal.node.data

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
    val type: ChatTask.Type,
    originalMessageChain: MessageChain,
    expireTime: Long,
    priority: Int,
    private val embeddingOptionsClazz: KClass<EMBEDDING_OPTIONS>
) : AbstractTask(AbstractTask.Type.EMBEDDING, originalMessageChain, expireTime, priority) {
    @get:JsonIgnore
    @Suppress("UNCHECKED_CAST")
    val chatOptions: EMBEDDING_OPTIONS? get() = this.originalMessageChain.messages.firstOrNull { embeddingOptionsClazz.isInstance(it) } as EMBEDDING_OPTIONS?

    enum class Type {
        OLLAMA,
        DEEPSEEK
    }
}

fun MessageChain.toOllamaEmbeddingTask(expireTime: Long, priority: Int = 0): EmbeddingTask<OllamaEmbeddingOptions> {
    return EmbeddingTask(
        type = ChatTask.Type.OLLAMA,
        originalMessageChain = this,
        expireTime = expireTime,
        priority = priority,
        embeddingOptionsClazz = OllamaEmbeddingOptions::class
    )
}