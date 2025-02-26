package com.lovelycatv.ai.crystal.node.data

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage

/**
 * When receiving a new task, the [MessageChain] should be packaged as [OllamaTask]
 * so that the [com.lovelycatv.ai.crystal.node.cron.OllamaTaskQueueConsumerCronJob] can perform the task
 *
 * @param originalMessageChain Original [MessageChain] object
 * @param expireTime Task max execution time.
 *                   If the execution time of the task exceeds expireTime,
 *                   the lock will automatically become available.
 *
 * @author lovelycat
 * @since 2025-02-26 22:15
 * @version 1.0
 */
data class OllamaTask(
    val originalMessageChain: MessageChain,
    val expireTime: Long
) {
    /**
     * SessionId in [MessageChain]
     */
    val requesterSessionId: String get() = this.originalMessageChain.sessionId

    val chatOptions: OllamaChatOptions? get() = this.originalMessageChain.messages
        .filterIsInstance<OllamaChatOptions>()
        .firstOrNull()

    val prompts: List<PromptMessage> get() = this.originalMessageChain.messages
        .filterIsInstance<PromptMessage>()
}

fun MessageChain.toOllamaTask(expireTime: Long): OllamaTask {
    return OllamaTask(
        originalMessageChain = this,
        expireTime = expireTime
    )
}