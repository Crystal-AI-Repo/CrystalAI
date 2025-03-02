package com.lovelycatv.ai.crystal.node.api.task

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.node.task.ChatTask
import com.lovelycatv.ai.crystal.node.task.EmbeddingTask

/**
 * @author lovelycat
 * @since 2025-03-03 01:08
 * @version 1.0
 */
class NodeTaskBuilderExtensions private constructor()

inline fun <reified O: AbstractChatOptions> NodeChatTaskBuilder(
    crossinline builder: (originalMessageChain: MessageChain
        ) -> ChatTask<O>)
    = object : NodeChatTaskBuilder<O> {
        override fun buildChatTask(originalMessageChain: MessageChain)
            = builder.invoke(originalMessageChain)
        override fun getOptionsClass(): Class<O> = O::class.java
    }


inline fun <reified O: AbstractEmbeddingOptions> NodeEmbeddingTaskBuilder(
    crossinline builder: (originalMessageChain: MessageChain) -> EmbeddingTask<O>
)
    = object : NodeEmbeddingTaskBuilder<O> {
        override fun buildEmbeddingTask(originalMessageChain: MessageChain)
            = builder.invoke(originalMessageChain)
        override fun getOptionsClass(): Class<O> = O::class.java
    }