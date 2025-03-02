package com.lovelycatv.ai.crystal.node.api.interfaces.model

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import org.springframework.ai.embedding.EmbeddingOptions

/**
 * @author lovelycat
 * @since 2025-03-01 16:10
 * @version 1.0
 */
interface EmbeddingOptions2SpringAIOptionsTranslator<EMBEDDING_OPTIONS: AbstractEmbeddingOptions, MODEL_OPTIONS: EmbeddingOptions>
    : ModelOptions2SpringAIOptionsTranslator<EMBEDDING_OPTIONS, MODEL_OPTIONS>