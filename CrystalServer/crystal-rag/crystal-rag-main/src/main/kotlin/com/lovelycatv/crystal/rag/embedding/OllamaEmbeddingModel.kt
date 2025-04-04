package com.lovelycatv.crystal.rag.embedding

import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModel
import org.springframework.ai.ollama.OllamaEmbeddingModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaOptions

/**
 * @author lovelycat
 * @since 2025-03-27 20:48
 * @version 1.0
 */
class OllamaEmbeddingModel(
    host: String,
    port: Int,
    model: String
) : EmbeddingModel {
    private val ollamaEmbeddingModel: OllamaEmbeddingModel

    init {
        ollamaEmbeddingModel = OllamaEmbeddingModel.builder()
            .ollamaApi(OllamaApi("$host:$port"))
            .defaultOptions(
                OllamaOptions.builder()
                    .model(model)
                    .build()
            )
            .build()
    }

    override fun embed(content: String): FloatArray {
        return this.ollamaEmbeddingModel.embed(content)
    }
}