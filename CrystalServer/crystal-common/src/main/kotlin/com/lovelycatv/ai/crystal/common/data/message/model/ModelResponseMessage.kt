package com.lovelycatv.ai.crystal.common.data.message.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

/**
 * @author lovelycat
 * @since 2025-03-01 16:34
 * @version 1.0
 */
abstract class ModelResponseMessage @JsonCreator constructor(
    @JsonProperty("success")
    var success: Boolean,
    @JsonProperty("message")
    var message: String?,
    type: Type
) : AbstractMessage(type) {
    companion object {
        inline fun <reified T: ModelResponseMessage> failed(message: String?, vararg additionalArgs: Any?): T {
            return T::class.primaryConstructor!!.call(*(listOf<Any?>(message) + additionalArgs.toList()).toTypedArray())
        }
    }

    @JsonIgnore
    fun isFinished() = GlobalConstants.Flags.isFinishedFlag(this.message)
}