package com.lovelycatv.ai.crystal.common.data.message.chat.options

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage

/**
 * @author lovelycat
 * @since 2025-02-28 14:23
 * @version 1.0
 */
abstract class AbstractChatOptions @JsonCreator constructor(
    @JSONField(name = "modelName")
    val modelName: String?,
    type: Type
) : AbstractMessage(type)