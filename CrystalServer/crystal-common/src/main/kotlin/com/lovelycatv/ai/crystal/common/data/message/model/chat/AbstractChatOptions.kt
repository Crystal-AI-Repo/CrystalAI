package com.lovelycatv.ai.crystal.common.data.message.model.chat

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.IMessageType
import com.lovelycatv.ai.crystal.common.data.message.model.AbstractModelOptions

/**
 * @author lovelycat
 * @since 2025-02-28 14:23
 * @version 1.0
 */
abstract class AbstractChatOptions @JsonCreator constructor(
    modelName: String?,
    type: IMessageType
) : AbstractModelOptions(modelName, type)