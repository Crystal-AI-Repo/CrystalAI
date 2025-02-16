package com.lovelycatv.ai.crystal.common.data.message.auth

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage

/**
 * @author lovelycat
 * @since 2025-02-16 19:42
 * @version 1.0
 */
@JsonTypeName("AUTHORIZE_RESPONSE")
class AuthorizeResponseMessage @JsonCreator constructor(
    @JSONField(name = "success")
    val success: Boolean,
    @JSONField(name = "message")
    val message: String?,
    @JSONField(name = "nodeId")
    val nodeId: String,
    @JSONField(name = "nodeName")
    val nodeName: String,
    @JSONField(name = "token")
    val token: String?,
    @JSONField(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) : AbstractMessage(Type.AUTHORIZE_RESPONSE) {
    companion object {
        fun success(nodeId: String, nodeName: String, token: String, message: String? = null): AuthorizeResponseMessage {
            return AuthorizeResponseMessage(
                success = true,
                message = message,
                nodeId = nodeId,
                nodeName = nodeName,
                token = token
            )
        }

        fun failed(nodeId: String, nodeName: String, message: String): AuthorizeResponseMessage {
            return AuthorizeResponseMessage(
                success = false,
                message = message,
                nodeId = nodeId,
                nodeName = nodeName,
                token = null
            )
        }
    }
}