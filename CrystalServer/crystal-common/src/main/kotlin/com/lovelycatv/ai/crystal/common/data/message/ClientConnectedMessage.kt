package com.lovelycatv.ai.crystal.common.data.message

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * @author lovelycat
 * @since 2025-02-16 22:31
 * @version 1.0
 */
@JsonTypeName("CLIENT_CONNECTED")
class ClientConnectedMessage @JsonCreator constructor(
    @JSONField(name = "nodeName")
    val nodeName: String
) : AbstractMessage(Type.CLIENT_CONNECTED)