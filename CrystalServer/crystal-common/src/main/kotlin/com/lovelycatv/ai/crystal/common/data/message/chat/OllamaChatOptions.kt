package com.lovelycatv.ai.crystal.common.data.message.chat

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage

@JsonTypeName("CHAT_OPTIONS")
data class OllamaChatOptions @JsonCreator constructor(
    @JSONField(name = "modelName")
    val modelName: String?,
    @JSONField(name = "temperature")
    val temperature: Double?
) : AbstractMessage(Type.CHAT_OPTIONS)