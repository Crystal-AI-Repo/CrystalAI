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
data class ChatRequestSessionContainer(
    val originalMessageChain: MessageChain,
    val nodeId: String,
    val timeout: Long,
    private val receivedResponses: MutableList<OllamaChatResponseMessage> = mutableListOf()
) {
    val startedTimestamp = System.currentTimeMillis()

    /**
     * As the timeout status will be checked when received messages.
     * (See [com.lovelycatv.ai.crystal.dispatcher.task.manager.ListenableTaskManager.onMessageReceived])
     * The default value is currentTimeMillis.
     */
    private var lastReceivedTimestamp = System.currentTimeMillis()

    private val _status: MutableLiveData<Status> = MutableLiveData(Status.PREPARED)

    val status: LiveData<Status> get() = this._status

    fun getResponses(): List<OllamaChatResponseMessage> = this.receivedResponses

    fun recentReceived() = this.receivedResponses.lastOrNull()

    fun addReceivedMessage(message: OllamaChatResponseMessage) {
        this._status.set(Status.RECEIVING)
        this.receivedResponses.add(message)
        this.lastReceivedTimestamp = System.currentTimeMillis()
    }

    fun setFinished() {
        this._status.set(Status.FINISHED)
    }

    fun checkAndSetTimeout() = (timeout > 0 && (System.currentTimeMillis() - this.lastReceivedTimestamp) > timeout).also {
        this._status.set(Status.TIMEOUT)
    }


    enum class Status {
        PREPARED,
        RECEIVING,
        FINISHED,
        TIMEOUT
    }
}