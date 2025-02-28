package com.lovelycatv.ai.crystal.dispatcher.data.node

import com.lovelycatv.ai.crystal.common.data.LiveData
import com.lovelycatv.ai.crystal.common.data.MutableLiveData
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.ChatResponseMessage

/**
 * @author lovelycat
 * @since 2025-02-27 01:06
 * @version 1.0
 */
data class ChatRequestSessionContainer(
    val originalMessageChain: MessageChain,
    val nodeId: String,
    val timeout: Long,
    private val receivedResponses: MutableList<ChatResponseMessage> = mutableListOf()
) {
    val startedTimestamp = System.currentTimeMillis()

    /**
     * As the timeout status will be checked when received messages.
     *
     * (See [com.lovelycatv.ai.crystal.dispatcher.task.manager.ListenableTaskManager.onMessageReceived])
     *
     * The default value is currentTimeMillis.
     */
    private var lastReceivedTimestamp = System.currentTimeMillis()

    private val _status: MutableLiveData<Status> = MutableLiveData(Status.PREPARED)

    val status: LiveData<Status> get() = this._status

    fun getResponses(): List<ChatResponseMessage> = this.receivedResponses

    fun recentReceived() = this.receivedResponses.lastOrNull()

    /**
     * While the session received message from node,
     * use this function to add the new message to the container
     * and set container status to [Status.RECEIVING].
     *
     * If received [ChatResponseMessage] is marked failed, then
     * the status of container will be set to [Status.FAILED].
     *
     * If the status of container is already set to [Status.FAILED],
     * then the function will do nothing.
     *
     * @param message Received [ChatResponseMessage] from node
     */
    fun addReceivedMessage(message: ChatResponseMessage) {
        if (this._status.get() == Status.FAILED) {
            return
        }

        if (message.success) {
            this._status.set(Status.RECEIVING)
        } else {
            this._status.set(Status.FAILED)
        }

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
        FAILED,
        TIMEOUT
    }
}