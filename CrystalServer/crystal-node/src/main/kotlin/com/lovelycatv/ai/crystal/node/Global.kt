package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.node.data.ChatTask
import com.lovelycatv.ai.crystal.node.exception.InvalidSessionIdException

object Global {
    object Variables {
        var currentNodeUUID: String? = null
        var dispatcherCommunicationPort: Int = -1

        @Volatile
        var isChatTaskRunning: Boolean = false
        @Volatile
        var currentRunningChatTask: ChatTask<*>? = null
        @Volatile
        var chatTaskStartedAt: Long = 0L
    }

    fun isChatTaskRunning(): Boolean {
        return Variables.isChatTaskRunning
    }

    @Synchronized
    fun lockChatRunningStatus(currentRunningChatTask: ChatTask<*>): Boolean {
        if (Variables.isChatTaskRunning) {
            return false
        }

        Variables.isChatTaskRunning = true
        Variables.currentRunningChatTask = currentRunningChatTask
        Variables.chatTaskStartedAt = System.currentTimeMillis()

        return true
    }

    @Synchronized
    fun unlockChatRunningStatus(requesterSessionId: String): Boolean {
        if (requesterSessionId.isBlank()) {
            throw InvalidSessionIdException(requesterSessionId)
        }

        return if (Variables.isChatTaskRunning && Variables.currentRunningChatTask?.requesterSessionId == requesterSessionId) {
            this.unlockChatRunningStatus()
            true
        } else {
            false
        }
    }

    @Synchronized
    private fun unlockChatRunningStatus() {
        Variables.isChatTaskRunning = false
        Variables.currentRunningChatTask = null
    }

    fun isNodeRegistered() = !Variables.currentNodeUUID.isNullOrEmpty()

    fun nodeRegistered(uuid: String, communicationPort: Int) {
        Variables.currentNodeUUID = uuid
        Variables.dispatcherCommunicationPort = communicationPort
    }

    fun clearNodeRegistrationStatus() {
        Variables.currentNodeUUID = null
        Variables.dispatcherCommunicationPort = -1
    }
}