package com.lovelycatv.ai.crystal.dispatcher.data.node

import com.lovelycatv.ai.crystal.common.data.LiveData
import com.lovelycatv.ai.crystal.common.data.MutableLiveData
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatResponseMessage

/**
 * @author lovelycat
 * @since 2025-02-27 01:06
 * @version 1.0
 */
data class OllamaChatRequestSessionContainer(
    val originalMessageChain: MessageChain,
    val nodeId: String,
    private val receivedResponses: MutableList<OllamaChatResponseMessage> = mutableListOf(),
    private val callbacks: Callbacks? = null
) {
    private val _status: MutableLiveData<Status> = MutableLiveData(Status.PREPARED)
    val status: LiveData<Status> get() = this._status

    fun getResponses(): List<OllamaChatResponseMessage> = this.receivedResponses

    private fun addReceivedMessage(message: OllamaChatResponseMessage) {
        this._status.set(Status.RECEIVING)
        this.receivedResponses.add(message)
    }

    fun addAndCallOnReceivedMessage(message: OllamaChatResponseMessage) {
        this.addReceivedMessage(message)
        this.callbacks?.onReceived(message, this)
    }

    fun addAndCallOnFinished(lastMessage: OllamaChatResponseMessage?) {
        lastMessage?.let {
            this.addReceivedMessage(lastMessage)
        }

        this.callbacks?.onFinished(this.copy())
    }

    interface Callbacks {
        fun onReceived(message: OllamaChatResponseMessage, container: OllamaChatRequestSessionContainer)

        fun onFinished(container: OllamaChatRequestSessionContainer)
    }

    enum class Status {
        PREPARED,
        RECEIVING,
        FINISHED,
        TIMEOUT
    }
}