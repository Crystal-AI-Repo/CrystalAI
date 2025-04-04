package com.lovelycatv.crystal.rag.manager

import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModel
import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModelType
import com.lovelycatv.crystal.rag.config.CrystalRAGConfig
import com.lovelycatv.crystal.rag.embedding.OllamaEmbeddingModel
import com.lovelycatv.crystal.rag.embedding.OpenApiEmbeddingModel
import com.lovelycatv.crystal.rag.entity.KnowledgeBase
import com.lovelycatv.crystal.rag.service.KnowledgeBaseService
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-03-27 20:42
 * @version 1.0
 */
@Component
class EmbeddingWorkerManager(
    private val knowledgeBaseService: KnowledgeBaseService,
    private val crystalRAGConfig: CrystalRAGConfig,
    private val nodeAccessKeyManager: NodeAccessKeyManager
) : InitializingBean {
    private val logger = logger()

    /**
     * { EmbeddingModelType -> { modelName: EmbeddingModel } }
     */
    private val _embeddingModels = mutableMapOf<EmbeddingModelType, MutableMap<String, EmbeddingModel>>().apply {
        EmbeddingModelType.entries.forEach {
            this[it] = mutableMapOf()
        }
    }
    val embeddingModels: Map<EmbeddingModelType, Map<String, EmbeddingModel>> get() = this._embeddingModels

    override fun afterPropertiesSet() {
        this.init()
    }

    private fun init() {
        knowledgeBaseService.list().filterNotNull().forEach {
            val embeddingOptions = it.getExplicitEmbeddingOptions()
            val modelName = embeddingOptions.modelName

            var flag = true
            if (this.embeddingModels.containsKey(embeddingOptions.type)) {
                if (this.embeddingModels[embeddingOptions.type]?.containsKey(modelName) == true) {
                    flag = false
                }
            }

            if (flag) {
                when (embeddingOptions.type) {
                    EmbeddingModelType.CRYSTAL_DISPATCHER -> {
                        _embeddingModels[EmbeddingModelType.CRYSTAL_DISPATCHER]!![modelName] =
                            OpenApiEmbeddingModel(
                                host = embeddingOptions.host ?: crystalRAGConfig.dispatcher.host,
                                port = embeddingOptions.port ?: crystalRAGConfig.dispatcher.port,
                                model = modelName,
                                apiKey = ""
                            )
                    }
                    EmbeddingModelType.CRYSTAL_NODE -> {
                        _embeddingModels[EmbeddingModelType.CRYSTAL_NODE]!![modelName] =
                            OpenApiEmbeddingModel(
                                host = embeddingOptions.host ?: crystalRAGConfig.node.host,
                                port = embeddingOptions.port ?: crystalRAGConfig.node.port,
                                model = modelName,
                                apiKey = nodeAccessKeyManager.getAccessKey(true)
                            )
                    }
                    EmbeddingModelType.STANDALONE_OLLAMA -> {
                        _embeddingModels[EmbeddingModelType.STANDALONE_OLLAMA]!![modelName] =
                            OllamaEmbeddingModel(
                                host = embeddingOptions.host ?: crystalRAGConfig.ollama.host,
                                port = embeddingOptions.port ?: crystalRAGConfig.ollama.port,
                                model = modelName
                            )
                    }
                    EmbeddingModelType.STANDALONE_OPENAI -> {
                        if (embeddingOptions.host == null || embeddingOptions.port == null) {
                            throw IllegalStateException("Knowledge base ${it.baseName} is using OpenAi Model but provides invalid host or port.")
                        }
                        if (embeddingOptions.apiKey.isNullOrBlank()) {
                            logger.warn("Knowledge base ${it.baseName} is using OpenAi Model but provides null or empty API Key.")
                        }
                        _embeddingModels[EmbeddingModelType.STANDALONE_OPENAI]!![modelName] =
                            OpenApiEmbeddingModel(embeddingOptions.host!!, embeddingOptions.port!!, modelName, embeddingOptions.apiKey ?: "")
                    }
                }
                logger.info("Embedding Model created, type: ${embeddingOptions.type}, modelName: ${embeddingOptions.modelName}")
            }
        }
    }

    fun getEmbeddingModel(knowledgeBase: KnowledgeBase): EmbeddingModel? {
        val opts = knowledgeBase.getExplicitEmbeddingOptions()
        return this.embeddingModels[opts.type]?.get(opts.modelName)
    }
}