package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.node.task.AbstractTask
import com.lovelycatv.ai.crystal.node.exception.InvalidSessionIdException

object Global {
    object Variables {
        var currentNodeUUID: String? = null
        var dispatcherCommunicationPort: Int = -1

        @Volatile
        var isTaskRunning: Boolean = false
        @Volatile
        var currentRunningTask: AbstractTask? = null
        @Volatile
        var taskStartedAt: Long = 0L
    }

    fun isTaskRunning(): Boolean {
        return Variables.isTaskRunning
    }

    @Synchronized
    fun lockTaskRunningStatus(currentRunningChatTask: AbstractTask): Boolean {
        if (Variables.isTaskRunning) {
            return false
        }

        Variables.isTaskRunning = true
        Variables.currentRunningTask = currentRunningChatTask
        Variables.taskStartedAt = System.currentTimeMillis()

        return true
    }

    @Synchronized
    fun unlockTaskRunningStatus(requesterSessionId: String): Boolean {
        if (requesterSessionId.isBlank()) {
            throw InvalidSessionIdException(requesterSessionId)
        }

        return if (Variables.isTaskRunning && Variables.currentRunningTask?.requesterSessionId == requesterSessionId) {
            this.forceUnlockTaskRunningStatus()
            true
        } else {
            false
        }
    }

    @Synchronized
    fun forceUnlockTaskRunningStatus() {
        Variables.isTaskRunning = false
        Variables.currentRunningTask = null
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