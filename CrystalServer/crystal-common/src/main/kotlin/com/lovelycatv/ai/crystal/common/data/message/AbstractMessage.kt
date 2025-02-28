package com.lovelycatv.ai.crystal.common.data.message

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeRequestMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.chat.options.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage

/**
 * @author lovelycat
 * @since 2025-02-16 19:21
 * @version 1.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AuthorizeRequestMessage::class, name = "AUTHORIZE_REQUEST"),
    JsonSubTypes.Type(value = AuthorizeResponseMessage::class, name = "AUTHORIZE_RESPONSE"),
    JsonSubTypes.Type(value = ClientConnectedMessage::class, name = "CLIENT_CONNECTED"),
    JsonSubTypes.Type(value = PromptMessage::class, name = "PROMPT"),
        JsonSubTypes.Type(value = OllamaChatOptions::class, name = "OLLAMA_CHAT_OPTIONS"),
        JsonSubTypes.Type(value = DeepSeekChatOptions::class, name = "DEEP_SEEK_CHAT_OPTIONS"),
    JsonSubTypes.Type(value = ChatResponseMessage::class, name = "CHAT_RESPONSE")
)
abstract class AbstractMessage @JsonCreator constructor(
    @JSONField(name = "type")
    val type: Type,
    @JSONField(name = "order")
    var order: Long = 0
) {
    enum class Type {
        PROMPT,
        OLLAMA_CHAT_OPTIONS,
        DEEP_SEEK_CHAT_OPTIONS,
        AUTHORIZE_REQUEST,
        AUTHORIZE_RESPONSE,
        CLIENT_CONNECTED,
        CHAT_RESPONSE
    }
}