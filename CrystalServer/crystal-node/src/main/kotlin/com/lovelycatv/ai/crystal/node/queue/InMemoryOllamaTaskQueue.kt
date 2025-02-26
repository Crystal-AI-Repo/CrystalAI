package com.lovelycatv.ai.crystal.node.queue

import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.data.OllamaTask
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author lovelycat
 * @since 2025-02-26 21:30
 * @version 1.0
 */
class InMemoryOllamaTaskQueue(
    initialCapacity: Int = 16
) : PriorityBlockingQueue<OllamaTask>(initialCapacity), OllamaTaskQueue {
    private val currentCapacity = AtomicInteger(initialCapacity)

    // For capacity expanding or pop process
    private val lock = ReentrantLock()

    override fun put(e: OllamaTask) {
        this.submitTask(e)
    }

    override fun take(): OllamaTask {
        throw IllegalAccessException("Please use the OllamaTaskQueue\$requireTask()")
    }

    override fun poll(): OllamaTask? {
        return this.requireTask()
    }

    /**
     * Submit tasks to the queue
     *
     * @param task [OllamaTask]
     */
    override fun submitTask(task: OllamaTask) {
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
     * @return [OllamaTask]
     */
    override fun requireTask(): OllamaTask? {
        lock.withLock {
            val peek: OllamaTask? = super.peek()
            return if (peek != null) {
                // The next task is existing
                if (Global.lockOllamaRunningStatus(peek.requesterSessionId, peek.expireTime)) {
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