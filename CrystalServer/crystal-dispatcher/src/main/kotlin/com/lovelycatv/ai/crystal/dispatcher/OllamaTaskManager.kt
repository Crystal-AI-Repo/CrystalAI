package com.lovelycatv.ai.crystal.dispatcher

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.dispatcher.data.node.OllamaChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.exception.DuplicateSessionIdException
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-27 01:04
 * @version 1.0
 */
@Component
class OllamaTaskManager {
    private val logger = logger()

    private val sessionResponseMap: MutableMap<String, OllamaChatRequestSessionContainer> = mutableMapOf()

    @Throws(DuplicateSessionIdException::class)
    fun pushSession(recipient: RegisteredNode, messageChain: MessageChain, callbacks: OllamaChatRequestSessionContainer.Callbacks?) {
        val t = this.getSession(messageChain.sessionId)
        if (t != null) {
            throw DuplicateSessionIdException(messageChain.sessionId, t.nodeId)
        }

        this.sessionResponseMap[messageChain.sessionId] = OllamaChatRequestSessionContainer(
            originalMessageChain = messageChain,
            nodeId = recipient.nodeId,
            receivedResponses = mutableListOf(),
            callbacks = callbacks
        )
    }

    fun getSession(sessionId: String): OllamaChatRequestSessionContainer? {
        return this.sessionResponseMap[sessionId]
    }

    fun onMessageReceived(messageChain: MessageChain, ollamaChatResponseMessage: OllamaChatResponseMessage) {
        val session = this.getSession(messageChain.sessionId)
        if (session != null) {
            if (ollamaChatResponseMessage.isFinished()) {
                session.addAndCallOnReceivedMessage(ollamaChatResponseMessage)
                session.addAndCallOnFinished(null)
            } else {
                updateSession(sessionId = messageChain.sessionId) {
                    this.apply {
                        this.addAndCallOnReceivedMessage(ollamaChatResponseMessage)
                    }
                }
            }
        } else {
            logger.warn("OllamaChatResponse received but the session: [${messageChain.sessionId}] is not found")
        }
    }

    private fun updateSession(sessionId: String, fx: OllamaChatRequestSessionContainer.() -> OllamaChatRequestSessionContainer): Boolean {
        return updateSession(this.getSession(sessionId), fx)
    }

    private fun updateSession(session: OllamaChatRequestSessionContainer?, fx: OllamaChatRequestSessionContainer.() -> OllamaChatRequestSessionContainer): Boolean {
        return if (session != null) {
            this.sessionResponseMap[session.originalMessageChain.sessionId] = fx.invoke(session)
            true
        } else {
            false
        }
    }

    private fun removeSession(sessionId: String) {
        this.sessionResponseMap.remove(sessionId)
    }
}