package com.lovelycatv.ai.crystal.common.data.message.chat.options

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("OLLAMA_CHAT_OPTIONS")
data class OllamaChatOptions @JsonCreator constructor(
    @JSONField(name = "modelName")
    val modelName: String?,
    @JSONField(name = "temperature")
    val temperature: Double?
) : AbstractChatOptions(Type.OLLAMA_CHAT_OPTIONS)