package com.lovelycatv.ai.crystal.common.data.message.chat.options

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("OLLAMA_CHAT_OPTIONS")
class OllamaChatOptions @JsonCreator constructor(
    modelName: String?,
    @JSONField(name = "temperature")
    val temperature: Double?
) : AbstractChatOptions(modelName, Type.OLLAMA_CHAT_OPTIONS)