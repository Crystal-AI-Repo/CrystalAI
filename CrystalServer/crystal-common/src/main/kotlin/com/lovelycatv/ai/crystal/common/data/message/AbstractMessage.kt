package com.lovelycatv.ai.crystal.common.data.message

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeRequestMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions

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
    JsonSubTypes.Type(value = DeepSeekChatOptions::class, name = "DEEPSEEK_CHAT_OPTIONS"),
    JsonSubTypes.Type(value = OllamaEmbeddingOptions::class, name = "OLLAMA_EMBEDDING_OPTIONS"),
    JsonSubTypes.Type(value = ChatResponseMessage::class, name = "CHAT_RESPONSE"),
    JsonSubTypes.Type(value = EmbeddingResponseMessage::class, name = "EMBEDDING_RESPONSE")
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
        DEEPSEEK_CHAT_OPTIONS,
        OLLAMA_EMBEDDING_OPTIONS,
        AUTHORIZE_REQUEST,
        AUTHORIZE_RESPONSE,
        CLIENT_CONNECTED,
        CHAT_RESPONSE,
        EMBEDDING_RESPONSE
    }
}