package com.lovelycatv.ai.crystal.common.data.message.chat.options

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * @author lovelycat
 * @since 2025-02-28 14:26
 * @version 1.0
 */
@JsonTypeName("DEEP_SEEK_CHAT_OPTIONS")
class DeepSeekChatOptions @JsonCreator constructor(
    modelName: String,
    @JSONField(name = "temperature")
    val temperature: Double?
) : AbstractChatOptions(modelName, Type.DEEP_SEEK_CHAT_OPTIONS)