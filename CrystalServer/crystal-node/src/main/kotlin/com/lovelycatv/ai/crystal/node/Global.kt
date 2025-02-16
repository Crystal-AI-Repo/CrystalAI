package com.lovelycatv.ai.crystal.node

object Global {
    object Variables {
        var currentNodeUUID: String? = null
        var dispatcherCommunicationPort: Int = -1
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