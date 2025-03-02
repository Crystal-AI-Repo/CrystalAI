package com.lovelycatv.ai.crystal.common.data.message.model

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.IMessageType

/**
 * @author lovelycat
 * @since 2025-03-01 15:53
 * @version 1.0
 */
abstract class AbstractModelOptions @JsonCreator constructor(
    @JSONField(name = "modelName")
    val modelName: String?,
    type: IMessageType
) : AbstractMessage(type)