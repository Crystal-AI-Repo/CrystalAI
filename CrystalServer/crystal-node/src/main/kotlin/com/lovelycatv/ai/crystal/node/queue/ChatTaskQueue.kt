package com.lovelycatv.ai.crystal.node.queue

import com.lovelycatv.ai.crystal.common.data.message.chat.options.AbstractChatOptions
import com.lovelycatv.ai.crystal.node.data.ChatTask

/**
 * @author lovelycat
 * @since 2025-02-26 22:27
 * @version 1.0
 */
interface ChatTaskQueue<CHAT_OPTIONS: AbstractChatOptions> {
    /**
     * Submit tasks to the queue
     *
     * @param task [ChatTask]
     */
    fun submitTask(task: ChatTask<out CHAT_OPTIONS>)

    /**
     * To make sure the popped message will be processed,
     * call this method will lock the ollama task running status.
     * (Only when the lock is available and queue is not empty)
     *
     * @return [ChatTask]
     */
    fun requireTask(): ChatTask<out CHAT_OPTIONS>?
}