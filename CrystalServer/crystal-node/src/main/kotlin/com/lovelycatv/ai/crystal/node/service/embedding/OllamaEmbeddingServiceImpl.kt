package com.lovelycatv.ai.crystal.node.service.embedding

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import org.springframework.ai.ollama.OllamaEmbeddingModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-03-01 15:58
 * @version 1.0
 */
@Service
class OllamaEmbeddingServiceImpl(
    private val nodeConfiguration: NodeConfiguration
) : OllamaEmbeddingService() {
    override fun buildEmbeddingModel(): OllamaEmbeddingModel {
        return OllamaEmbeddingModel.builder()
            .ollamaApi(OllamaApi(nodeConfiguration.ollama.baseUrl))
            .defaultOptions(
                this.translate(OllamaEmbeddingOptions(
                    modelName = nodeConfiguration.ollama.defaultModel
                ))
            )
            .build()
    }
}