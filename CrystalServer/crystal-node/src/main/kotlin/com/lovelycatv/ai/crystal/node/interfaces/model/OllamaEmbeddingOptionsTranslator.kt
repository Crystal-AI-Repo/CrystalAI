package com.lovelycatv.ai.crystal.node.interfaces.model

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.node.api.interfaces.model.EmbeddingOptions2SpringAIOptionsTranslator
import org.springframework.ai.ollama.api.OllamaOptions

/**
 * @author lovelycat
 * @since 2025-03-01 16:03
 * @version 1.0
 */
class OllamaEmbeddingOptionsTranslator :
    EmbeddingOptions2SpringAIOptionsTranslator<OllamaEmbeddingOptions, OllamaOptions> {
    override fun translate(original: OllamaEmbeddingOptions?): OllamaOptions {
        return OllamaOptions.builder().apply {
            original?.modelName?.let {
                this.model(it)
            }
        }.build()
    }
}