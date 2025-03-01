package com.lovelycatv.ai.crystal.common.data.message.model.embedding

import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions

/**
 * @author lovelycat
 * @since 2025-03-01 15:17
 * @version 1.0
 */
@JsonTypeName("OLLAMA_EMBEDDING_OPTIONS")
class OllamaEmbeddingOptions(
    modelName: String?
) : AbstractEmbeddingOptions(modelName, Type.OLLAMA_EMBEDDING_OPTIONS)