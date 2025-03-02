package com.lovelycatv.ai.crystal.node.task

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage


/**
 * When receiving a new chat task, the [MessageChain] should be packaged as [ChatTask]
 * so that the [com.lovelycatv.ai.crystal.node.cron.TaskQueueConsumerCronJob] can perform the task
 *
 * @param originalMessageChain Original [MessageChain] object
 * @param expireTime Task max execution time.
 *                   If the execution time of the task exceeds expireTime,
 *                   the lock will automatically become available.
 * @param priority Task has higher priority than others will be executed first.
 *
 * @author lovelycat
 * @since 2025-02-28 19:51
 * @version 1.0
 */
abstract class AbstractTask(
    val taskType: Type,
    val originalMessageChain: MessageChain,
    val expireTime: Long,
    val priority: Int,
) : Comparable<AbstractTask>{
    /**
     * SessionId in [MessageChain]
     */
    @get:JsonIgnore
    val requesterSessionId: String get() = this.originalMessageChain.sessionId

    @get:JsonIgnore
    val requesterStreamId: String? get() = this.originalMessageChain.streamId

    @get:JsonIgnore
    val prompts: List<PromptMessage> get() = this.originalMessageChain.messages
        .filterIsInstance<PromptMessage>()

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: AbstractTask): Int {
        return this.priority - other.priority
    }

    enum class Type {
        CHAT,
        EMBEDDING
    }
}