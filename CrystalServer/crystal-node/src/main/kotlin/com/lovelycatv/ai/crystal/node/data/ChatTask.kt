package com.lovelycatv.ai.crystal.node.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.options.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import kotlin.reflect.KClass

/**
 * When receiving a new task, the [MessageChain] should be packaged as [ChatTask]
 * so that the [com.lovelycatv.ai.crystal.node.cron.ChatTaskQueueConsumerCronJob] can perform the task
 *
 * @param originalMessageChain Original [MessageChain] object
 * @param expireTime Task max execution time.
 *                   If the execution time of the task exceeds expireTime,
 *                   the lock will automatically become available.
 * @param priority Task has higher priority than others will be executed first.
 *
 * @author lovelycat
 * @since 2025-02-26 22:15
 * @version 1.0
 */
class ChatTask<CHAT_OPTIONS: AbstractChatOptions>(
    val type: Type,
    val originalMessageChain: MessageChain,
    val expireTime: Long,
    val priority: Int,
    private val chatOptionsClazz: KClass<CHAT_OPTIONS>
) : AbstractTask(AbstractTask.Type.CHAT), Comparable<ChatTask<CHAT_OPTIONS>> {
    /**
     * SessionId in [MessageChain]
     */
    @get:JsonIgnore
    val requesterSessionId: String get() = this.originalMessageChain.sessionId

    @get:JsonIgnore
    @Suppress("UNCHECKED_CAST")
    val chatOptions: CHAT_OPTIONS? get() = this.originalMessageChain.messages.firstOrNull { chatOptionsClazz.isInstance(it) } as CHAT_OPTIONS?

    @get:JsonIgnore
    val prompts: List<PromptMessage> get() = this.originalMessageChain.messages
        .filterIsInstance<PromptMessage>()

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ChatTask<CHAT_OPTIONS>): Int {
        return this.priority - other.priority
    }

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