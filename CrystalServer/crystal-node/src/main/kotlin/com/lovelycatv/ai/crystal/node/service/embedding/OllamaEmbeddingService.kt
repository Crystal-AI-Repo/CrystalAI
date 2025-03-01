package com.lovelycatv.ai.crystal.node.service.embedding

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.node.interfaces.model.OllamaEmbeddingOptionsTranslator
import com.lovelycatv.ai.crystal.node.service.embedding.base.AbstractSpringAIEmbeddingService
import org.springframework.ai.ollama.OllamaEmbeddingModel
import org.springframework.ai.ollama.api.OllamaOptions

/**
 * @author lovelycat
 * @since 2025-03-01 14:53
 * @version 1.0
 */
abstract class OllamaEmbeddingService :
    AbstractSpringAIEmbeddingService<OllamaEmbeddingModel, OllamaOptions, OllamaEmbeddingOptions>(
        defaultEmbeddingModel = null,
        translatorDelegate = OllamaEmbeddingOptionsTranslator()
    )