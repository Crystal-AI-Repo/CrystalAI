package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.node.exception.InvalidSessionIdException

object Global {
    object Variables {
        var currentNodeUUID: String? = null
        var dispatcherCommunicationPort: Int = -1

        @Volatile
        var isOllamaTaskRunning: Boolean = false
        @Volatile
        var ollamaTaskRequesterSessionId: String? = null
        @Volatile
        var ollamaTaskStartedAt: Long = 0L
        @Volatile
        var ollamaTaskExpireTime: Long = 0L
    }

    @Synchronized
    fun lockOllamaRunningStatus(requesterSessionId: String, expireTime: Long): Boolean {
        if (requesterSessionId.isBlank()) {
            throw InvalidSessionIdException(requesterSessionId)
        }

        if (Variables.isOllamaTaskRunning) {
            return if (Variables.ollamaTaskExpireTime > 0 && System.currentTimeMillis() - Variables.ollamaTaskStartedAt > Variables.ollamaTaskExpireTime) {
                // The last task is expired
                this.unlockOllamaRunningStatus()
                true
            } else {
                false
            }
        }

        Variables.isOllamaTaskRunning = true
        Variables.ollamaTaskRequesterSessionId = requesterSessionId
        Variables.ollamaTaskStartedAt = System.currentTimeMillis()
        Variables.ollamaTaskExpireTime = expireTime

        return true
    }

    @Synchronized
    fun unlockOllamaRunningStatus(requesterSessionId: String): Boolean {
        if (requesterSessionId.isBlank()) {
            throw InvalidSessionIdException(requesterSessionId)
        }

        return if (Variables.isOllamaTaskRunning && Variables.ollamaTaskRequesterSessionId == requesterSessionId) {
            this.unlockOllamaRunningStatus()
            true
        } else {
            false
        }
    }

    @Synchronized
    private fun unlockOllamaRunningStatus() {
        Variables.isOllamaTaskRunning = false
        Variables.ollamaTaskRequesterSessionId = null
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