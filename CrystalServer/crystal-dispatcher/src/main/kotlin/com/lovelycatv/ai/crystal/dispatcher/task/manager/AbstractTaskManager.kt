package com.lovelycatv.ai.crystal.dispatcher.task.manager

import com.lovelycatv.ai.crystal.common.annotations.CallSuper
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.exception.DuplicateSessionIdException
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import java.util.*

/**
 * @author lovelycat
 * @since 2025-02-28 02:34
 * @version 1.0
 */
abstract class AbstractTaskManager(
    private val nodeManager: AbstractNodeManager
) {
    private val sessionResponseMap: MutableMap<String, ChatRequestSessionContainer> = mutableMapOf()
    val currentSessions: Collection<ChatRequestSessionContainer> get() = this.sessionResponseMap.values

    @CallSuper
    @Throws(DuplicateSessionIdException::class)
    open fun pushSession(
        recipient: RegisteredNode,
        messageChain: MessageChain,
        timeout: Long = 0
    ): String {
        val sessionId = messageChain.sessionId

        val t = this.getSession(sessionId)
        if (t != null) {
            throw DuplicateSessionIdException(sessionId, t.nodeId)
        }

        this.sessionResponseMap[sessionId] = ChatRequestSessionContainer(
            originalMessageChain = messageChain,
            nodeId = recipient.nodeId,
            receivedResponses = mutableListOf(),
            timeout = timeout
        )

        return sessionId
    }

    fun getSession(sessionId: String): ChatRequestSessionContainer? {
        return this.sessionResponseMap[sessionId]
    }

    protected fun updateSession(sessionId: String, fx: ChatRequestSessionContainer.() -> ChatRequestSessionContainer): Boolean {
        return updateSession(this.getSession(sessionId), fx)
    }

    private fun updateSession(session: ChatRequestSessionContainer?, fx: ChatRequestSessionContainer.() -> ChatRequestSessionContainer): Boolean {
        return if (session != null) {
            this.sessionResponseMap[session.originalMessageChain.sessionId] = fx.invoke(session)
            true
        } else {
            false
        }
    }

    @CallSuper
    protected open fun removeSession(sessionId: String) {
        this.sessionResponseMap.remove(sessionId)
    }

    abstract fun onMessageReceived(messageChain: MessageChain, chatResponseMessage: ChatResponseMessage)
}