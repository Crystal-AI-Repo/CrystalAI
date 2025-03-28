package com.lovelycatv.ai.crystal.node.service.embedding.base

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.api.interfaces.model.EmbeddingOptions2SpringAIOptionsTranslator
import com.lovelycatv.ai.crystal.node.data.AbstractEmbeddingResult
import com.lovelycatv.ai.crystal.node.data.SpringAIEmbeddingResult
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingOptions
import org.springframework.ai.embedding.EmbeddingRequest

/**
 * @author lovelycat
 * @since 2025-03-01 15:30
 * @version 1.0
 */
abstract class AbstractSpringAIEmbeddingService<EMBEDDING_MODEL: EmbeddingModel, MODEL_OPTIONS: EmbeddingOptions, OPTIONS: AbstractEmbeddingOptions>(
    private var defaultEmbeddingModel: EMBEDDING_MODEL? = null,
    private val translatorDelegate: EmbeddingOptions2SpringAIOptionsTranslator<OPTIONS, MODEL_OPTIONS>
) : AbstractEmbeddingService<OPTIONS, SpringAIEmbeddingResult>(),
    EmbeddingOptions2SpringAIOptionsTranslator<OPTIONS, MODEL_OPTIONS> by translatorDelegate
{
    private val logger = logger()

    abstract fun buildEmbeddingModel(): EMBEDDING_MODEL

    override suspend fun embedding(options: OPTIONS?, content: List<PromptMessage>): SpringAIEmbeddingResult {
        if (this.defaultEmbeddingModel == null) {
            this.defaultEmbeddingModel = this.buildEmbeddingModel()
        }

        val response = this.defaultEmbeddingModel!!.call(
            EmbeddingRequest(
                content.map { prompt ->
                    prompt.message.filter {
                        it.type == PromptMessage.Content.Type.TEXT
                    }.joinToString(separator = " ") { it.stringContent() }
                },
                this.translate(options)
            )
        )

        return SpringAIEmbeddingResult(
            metadata = AbstractEmbeddingResult.Metadata(
                promptTokens = response.metadata.usage.promptTokens,
                totalTokens = response.metadata.usage.totalTokens
            ),
            response.results.map { it.output.map { it.toDouble() }.toDoubleArray() }
        )
    }
}