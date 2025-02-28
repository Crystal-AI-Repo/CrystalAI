package com.lovelycatv.ai.crystal.dispatcher.task.manager

import com.lovelycatv.ai.crystal.common.annotations.CallSuper
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import kotlinx.coroutines.*
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-02-28 02:35
 * @version 1.0
 */
abstract class ListenableTaskManager(
    nodeManager: AbstractNodeManager
) : AbstractTaskManager(nodeManager) {
    private val logger = logger()

    private val sessionSubscribers: MutableMap<String, MutableMap<String, Subscriber>> = mutableMapOf()

    private val timeoutChecker = CoroutineScope(Dispatchers.IO + CoroutineName("SessionTimeoutChecker"))

    @CallSuper
    override fun pushSession(recipient: RegisteredNode, messageChain: MessageChain, timeout: Long): String {
        val sessionId = super.pushSession(recipient, messageChain, timeout)

        // Start a timeout timer
        if (timeout > 0) {
            timeoutChecker.launch {
                while (true) {
                    val container = super.getSession(sessionId) ?: break
                    if (System.currentTimeMillis() - container.startedTimestamp > timeout) {
                        logger.warn("Session [${sessionId}] timeout, expected: ${timeout}ms")
                        container.checkAndSetTimeout()
                        this@ListenableTaskManager.dispatchSubscriptions(sessionId, container)
                        super.removeSession(sessionId)
                        this@ListenableTaskManager.unsubscribeAll(sessionId)
                    }
                    delay(100)
                }
            }
        }

        return sessionId
    }

    /**
     * Subscribe a session
     *
     * @param sessionId sessionId
     * @param subscriber [Subscriber]
     * @return SubscriberId
     */
    fun subscribe(sessionId: String, subscriber: Subscriber): String? {
        super.getSession(sessionId) ?: return null
        val subscriberId = UUID.randomUUID().toString()

        val map = this.sessionSubscribers.computeIfAbsent(sessionId) { mutableMapOf() }
        map[subscriberId] = subscriber

        return subscriberId
    }

    fun unsubscribe(sessionId: String, subscriberId: String): Boolean {
        return this.sessionSubscribers
            .filter { it.key == sessionId }
            .firstNotNullOfOrNull { it.value }
            ?.remove(subscriberId) != null
    }

    private fun unsubscribeAll(sessionId: String) {
        this.sessionSubscribers[sessionId]?.clear()
        this.sessionSubscribers.remove(sessionId)
    }

    @CallSuper
    override fun onMessageReceived(messageChain: MessageChain, chatResponseMessage: ChatResponseMessage) {
        val sessionId = messageChain.sessionId

        val sessionContainer = super.getSession(sessionId)

        if (sessionContainer != null) {
            if (sessionContainer.checkAndSetTimeout()) {
                this.dispatchSubscriptions(sessionId, sessionContainer)
                this.removeSession(sessionId)
                logger.warn("Session [${sessionId}] timeout, expected: ${sessionContainer.timeout}ms, received: ${chatResponseMessage.toJSONString()}")
            } else if (chatResponseMessage.isFinished()) {
                sessionContainer.addReceivedMessage(chatResponseMessage)
                sessionContainer.setFinished()
                this.dispatchSubscriptions(sessionId, sessionContainer)
                this.removeSession(sessionId)
                logger.info("Session [${sessionId}] finished")
            } else {
                super.updateSession(sessionId = messageChain.sessionId) {
                    this.apply {
                        // This function will detect whether the response is marked success and update container status automatically
                        this.addReceivedMessage(chatResponseMessage)
                    }
                }
                this.dispatchSubscriptions(sessionId, sessionContainer)

                if (!chatResponseMessage.success) {
                    this.removeSession(sessionId)
                    logger.warn("Session [${sessionId}] received failure message: ${chatResponseMessage.toJSONString()}")
                }
            }
        } else {
            this.dispatchSubscriptions(sessionId, null)
            this.unsubscribeAll(sessionId)
            logger.warn("OllamaChatResponse received but the session: [${messageChain.sessionId}] is not found, received: ${messageChain.toJSONString()}")
        }
    }

    private fun dispatchSubscriptions(sessionId: String, container: ChatRequestSessionContainer?) {
        val subscribers = this.sessionSubscribers[sessionId]?.values ?: return

        if (container == null) {
            subscribers
                .filterIsInstance<OnFailedSubscriber>()
                .forEach {
                    it.onFailed(null, null)
                }
            return
        }

        val sessionContainer = super.getSession(sessionId) ?: return

        when (container.status.get()) {
            ChatRequestSessionContainer.Status.PREPARED -> {}
            ChatRequestSessionContainer.Status.RECEIVING -> {
                val received = sessionContainer.recentReceived()!!

                subscribers
                    .filterIsInstance<OnReceivedSubscriber>()
                    .forEach {
                        it.onReceived(sessionContainer, received)
                    }
            }
            ChatRequestSessionContainer.Status.FINISHED -> {
                subscribers
                    .filterIsInstance<OnFinishedSubscriber>()
                    .forEach {
                        it.onFinished(sessionContainer)
                    }
            }
            ChatRequestSessionContainer.Status.FAILED -> {
                val received = sessionContainer.recentReceived()!!

                subscribers
                    .filterIsInstance<OnFailedSubscriber>()
                    .forEach {
                        it.onFailed(sessionContainer, received)
                    }
            }
            ChatRequestSessionContainer.Status.TIMEOUT -> {
                subscribers
                    .filterIsInstance<OnTimeoutSubscriber>()
                    .forEach {
                        it.onTimeout(sessionContainer)
                    }
            }
        }
    }

    @CallSuper
    override fun removeSession(sessionId: String) {
        super.removeSession(sessionId)
        this.unsubscribeAll(sessionId)
    }

    interface Subscriber

    interface SimpleSubscriber : OnReceivedSubscriber, OnFinishedSubscriber, OnFailedSubscriber

    fun interface OnReceivedSubscriber : Subscriber {
        fun onReceived(container: ChatRequestSessionContainer, message: ChatResponseMessage)
    }

    fun interface OnFinishedSubscriber : Subscriber {
        fun onFinished(container: ChatRequestSessionContainer)
    }

    fun interface OnFailedSubscriber : Subscriber {
        fun onFailed(container: ChatRequestSessionContainer?, failedMessage: ChatResponseMessage?)
    }

    fun interface OnTimeoutSubscriber : Subscriber {
        fun onTimeout(container: ChatRequestSessionContainer)
    }
}