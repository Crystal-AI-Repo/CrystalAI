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
@JsonTypeName("AUTHORIZE_REQUEST")
data class AuthorizeRequestMessage @JsonCreator constructor(
    @JSONField(name = "nodeName")
    val nodeName: String,
    @JSONField(name = "secretKey")
    val secretKey: String
) : AbstractMessage(Type.AUTHORIZE_REQUEST)