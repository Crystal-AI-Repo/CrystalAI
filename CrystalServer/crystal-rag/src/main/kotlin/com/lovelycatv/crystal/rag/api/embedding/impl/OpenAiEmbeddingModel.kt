package com.lovelycatv.crystal.rag.api.embedding.impl

import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModel
import org.springframework.ai.document.MetadataMode
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingOptions
import org.springframework.ai.openai.api.OpenAiApi

/**
 * @author lovelycat
 * @since 2025-03-27 20:54
 * @version 1.0
 */
class OpenAiEmbeddingModel(
    host: String,
    port: Int,
    model: String,
    apiKey: String
) : EmbeddingModel {
    private val openAiEmbeddingModel: OpenAiEmbeddingModel

    init {
        openAiEmbeddingModel = OpenAiEmbeddingModel(
            OpenAiApi("$host:$port", apiKey),
            MetadataMode.EMBED,
            OpenAiEmbeddingOptions.builder()
                .model(model)
                .build()
        )
    }

    override fun embed(content: String): FloatArray {
        return this.openAiEmbeddingModel.embed(content)
    }
}