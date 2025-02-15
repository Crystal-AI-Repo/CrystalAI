package com.lovelycatv.ai.crystal.node

object Global {
    object Constants {

    }

    object Variables {
        var currentNodeUUID: String? = null
    }

    fun isNodeRegistered() = !Variables.currentNodeUUID.isNullOrEmpty()

    fun nodeRegistered(uuid: String) {
        Variables.currentNodeUUID = uuid
    }

    fun clearNodeRegistrationStatus() {
        Variables.currentNodeUUID = null
    }
}