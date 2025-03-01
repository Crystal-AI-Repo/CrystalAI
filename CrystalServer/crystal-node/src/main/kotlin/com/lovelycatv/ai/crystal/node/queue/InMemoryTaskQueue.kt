package com.lovelycatv.ai.crystal.node.queue

import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.data.AbstractTask
import com.lovelycatv.ai.crystal.node.data.ChatTask
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author lovelycat
 * @since 2025-03-01 16:51
 * @version 1.0
 */
class InMemoryTaskQueue<TASK: AbstractTask>(
    initialCapacity: Int = 16
) : PriorityBlockingQueue<TASK>(initialCapacity), TaskQueue<TASK> {
    private val currentCapacity = AtomicInteger(initialCapacity)

    // For capacity expanding or pop process
    private val lock = ReentrantLock()

    override fun put(e: TASK) {
        this.submitTask(e)
    }

    override fun take(): TASK {
        throw IllegalAccessException("Please use the OllamaTaskQueue\$requireTask()")
    }

    override fun poll(): TASK? {
        return this.requireTask()
    }

    override fun glance(): List<TASK> {
        return this.toList()
    }

    /**
     * Submit tasks to the queue
     *
     * @param task [ChatTask]
     */
    override fun submitTask(task: TASK) {
        lock.withLock {
            if (super.size >= currentCapacity.get()) {
                this.expandQueue()
            }
            super.put(task)
        }
    }

    /**
     * To make sure the popped message will be processed,
     * call this method will lock the ollama task running status.
     * (Only when the lock is available and queue is not empty)
     *
     * @return [ChatTask]
     */
    override fun requireTask(): TASK? {
        lock.withLock {
            val peek: TASK? = super.peek()
            return if (peek != null) {
                // The next task is existing
                if (Global.lockTaskRunningStatus(peek)) {
                    // Lock is acquired
                    super.poll()
                } else {
                    // Lock is using by another task
                    null
                }
            } else {
                // Lock is using by another task
                null
            }
        }
    }

    private fun expandQueue() {
        val newCapacity = currentCapacity.get() * 2
        val newQueue = PriorityBlockingQueue(newCapacity, comparator())

        newQueue.addAll(this)
        currentCapacity.set(newCapacity)

        this.clear()
        this.addAll(newQueue)
    }
}