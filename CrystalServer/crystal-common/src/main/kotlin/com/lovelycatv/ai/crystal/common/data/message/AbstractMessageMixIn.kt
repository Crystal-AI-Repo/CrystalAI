package com.lovelycatv.ai.crystal.common.data.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeRequestMessage
import com.lovelycatv.ai.crystal.common.data.message.auth.AuthorizeResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions

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
abstract class AbstractMessageMixIn