package com.lovelycatv.ai.crystal.node.queue

import com.lovelycatv.ai.crystal.node.data.OllamaTask

/**
 * @author lovelycat
 * @since 2025-02-26 22:27
 * @version 1.0
 */
interface OllamaTaskQueue {
    /**
     * Submit tasks to the queue
     *
     * @param task [OllamaTask]
     */
    fun submitTask(task: OllamaTask)

    /**
     * To make sure the popped message will be processed,
     * call this method will lock the ollama task running status.
     * (Only when the lock is available and queue is not empty)
     *
     * @return [OllamaTask]
     */
    fun requireTask(): OllamaTask?
}