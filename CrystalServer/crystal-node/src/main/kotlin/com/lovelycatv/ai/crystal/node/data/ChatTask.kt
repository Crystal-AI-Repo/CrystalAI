package com.lovelycatv.ai.crystal.node.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-02-26 22:15
 * @version 1.0
 */
class ChatTask<CHAT_OPTIONS: AbstractChatOptions>(
    val type: Type,
    originalMessageChain: MessageChain,
    expireTime: Long,
    priority: Int,
    private val chatOptionsClazz: KClass<CHAT_OPTIONS>
) : AbstractTask(AbstractTask.Type.CHAT, originalMessageChain, expireTime, priority) {
    @get:JsonIgnore
    @Suppress("UNCHECKED_CAST")
    val chatOptions: CHAT_OPTIONS? get() = this.originalMessageChain.messages.firstOrNull { chatOptionsClazz.isInstance(it) } as CHAT_OPTIONS?

    enum class Type {
        OLLAMA,
        DEEPSEEK
    }
}

fun MessageChain.toOllamaTask(expireTime: Long, priority: Int = 0): ChatTask<OllamaChatOptions> {
    return ChatTask(
        type = ChatTask.Type.OLLAMA,
        originalMessageChain = this,
        expireTime = expireTime,
        priority = priority,
        chatOptionsClazz = OllamaChatOptions::class
    )
}

fun MessageChain.toDeepSeekTask(expireTime: Long, priority: Int = 0): ChatTask<DeepSeekChatOptions> {
    return ChatTask(
        type = ChatTask.Type.DEEPSEEK,
        originalMessageChain = this,
        expireTime = expireTime,
        priority = priority,
        chatOptionsClazz = DeepSeekChatOptions::class
    )
}