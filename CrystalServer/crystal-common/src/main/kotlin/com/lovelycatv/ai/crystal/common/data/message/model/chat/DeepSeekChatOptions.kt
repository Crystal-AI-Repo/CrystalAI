package com.lovelycatv.ai.crystal.common.data.message.model.chat

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions

/**
 * @author lovelycat
 * @since 2025-02-28 14:26
 * @version 1.0
 */
@JsonTypeName("DEEPSEEK_CHAT_OPTIONS")
class DeepSeekChatOptions @JsonCreator constructor(
    modelName: String,
    @JSONField(name = "temperature")
    val temperature: Double?
) : AbstractChatOptions(modelName, Type.DEEPSEEK_CHAT_OPTIONS)