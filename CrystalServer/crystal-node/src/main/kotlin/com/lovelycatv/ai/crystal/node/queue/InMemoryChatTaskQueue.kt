package com.lovelycatv.ai.crystal.node.queue

import com.lovelycatv.ai.crystal.common.data.message.chat.options.AbstractChatOptions
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.data.ChatTask
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author lovelycat
 * @since 2025-02-26 21:30
 * @version 1.0
 */
class InMemoryChatTaskQueue(
    initialCapacity: Int = 16
) : PriorityBlockingQueue<ChatTask<out AbstractChatOptions>>(initialCapacity), DefaultChatTaskQueue {
    private val currentCapacity = AtomicInteger(initialCapacity)

    // For capacity expanding or pop process
    private val lock = ReentrantLock()

    override fun put(e: ChatTask<out AbstractChatOptions>) {
        this.submitTask(e)
    }

    override fun take(): ChatTask<out AbstractChatOptions> {
        throw IllegalAccessException("Please use the OllamaTaskQueue\$requireTask()")
    }

    override fun poll(): ChatTask<out AbstractChatOptions>? {
        return this.requireTask()
    }

    /**
     * Submit tasks to the queue
     *
     * @param task [ChatTask]
     */
    override fun submitTask(task: ChatTask<out AbstractChatOptions>) {
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
    override fun requireTask(): ChatTask<out AbstractChatOptions>? {
        lock.withLock {
            val peek: ChatTask<out AbstractChatOptions>? = super.peek()
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