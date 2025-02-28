package com.lovelycatv.ai.crystal.node.queue

import com.lovelycatv.ai.crystal.node.data.AbstractTask

/**
 * @author lovelycat
 * @since 2025-02-28 19:51
 * @version 1.0
 */
interface TaskQueue {
    /**
     * Inspect the task queue
     *
     * @return All tasks in the queue
     */
    fun glance(): List<AbstractTask>
}