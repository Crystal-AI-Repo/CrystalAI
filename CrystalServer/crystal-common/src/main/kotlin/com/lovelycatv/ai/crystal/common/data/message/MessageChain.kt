package com.lovelycatv.ai.crystal.common.data.message

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

/**
 * @author lovelycat
 * @since 2025-02-16 19:22
 * @version 1.0
 */
data class MessageChain @JsonCreator constructor(
    @JSONField(name = "messages")
    val messages: List<AbstractMessage> = listOf(),
    @JSONField(name = "sessionId")
    val sessionId: String = UUID.randomUUID().toString(),
    @JSONField(name = "streamId")
    val streamId: String? = null,
    @JSONField(name = "token")
    val token: String? = null,
    @JSONField(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    @JsonIgnore
    fun isEmpty() = this.messages.isEmpty()

    @JsonIgnore
    fun withToken() = !this.token.isNullOrEmpty()

    @JsonIgnore
    fun isStream() = !this.streamId.isNullOrEmpty()

    class Builder {
        private val messages = mutableListOf<AbstractMessage>()
        private var sessionId: String = UUID.randomUUID().toString()
        private var streamId: String? = null
        private var token: String? = null
        private var timestamp: Long = System.currentTimeMillis()

        fun sessionId(sessionId: String = UUID.randomUUID().toString()): Builder {
            this.sessionId = sessionId
            return this
        }

        fun streamId(streamId: String? = UUID.randomUUID().toString()): Builder {
            this.streamId = streamId
            return this
        }

        fun withToken(token: String?): Builder {
            this.token = token
            return this
        }

        fun timestamp(timestamp: Long = System.currentTimeMillis()): Builder {
            this.timestamp = timestamp
            return this
        }

        fun addMessage(message: AbstractMessage): Builder {
            messages.add(message)
            return this
        }

        fun removeFirstMessage() = messages.removeFirstOrNull().run { this }

        fun removeLastMessage() = messages.removeLastOrNull().run { this }

        fun build() = MessageChain(messages, sessionId, streamId, token, timestamp)
    }
}