package com.lovelycatv.ai.crystal.node.queue

import com.lovelycatv.ai.crystal.node.task.AbstractTask
import com.lovelycatv.ai.crystal.node.task.ChatTask

/**
 * @author lovelycat
 * @since 2025-02-28 19:51
 * @version 1.0
 */
interface TaskQueue<TASK: AbstractTask> {
    /**
     * Submit tasks to the queue
     *
     * @param task [ChatTask]
     */
    fun submitTask(task: TASK)

    /**
     * To make sure the popped message will be processed,
     * call this method will lock the ollama task running status.
     * (Only when the lock is available and queue is not empty)
     *
     * @return [ChatTask]
     */
    fun requireTask(): TASK?

    /**
     * Inspect the task queue
     *
     * @return All tasks in the queue
     */
    fun glance(): List<TASK>
}