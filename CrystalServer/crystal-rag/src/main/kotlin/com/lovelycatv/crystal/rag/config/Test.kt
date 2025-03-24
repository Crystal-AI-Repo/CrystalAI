package com.lovelycatv.crystal.rag.config

import org.elasticsearch.client.RestClient
import org.springframework.ai.document.Document
import org.springframework.ai.document.MetadataMode
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.embedding.EmbeddingResponse
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions
import org.springframework.ai.vectorstore.elasticsearch.SimilarityFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * @author lovelycat
 * @since 2025-03-25 14:41
 * @version 1.0
 */
@Configuration
class Test {
    @Bean
    fun vectorStore(restClient: RestClient, embeddingModel: EmbeddingModel): VectorStore {
        val options = ElasticsearchVectorStoreOptions()
        options.indexName = "custom-index"
        options.similarity = SimilarityFunction.cosine
        options.dimensions = 1536

        return ElasticsearchVectorStore.builder(restClient, embeddingModel)
            .options(options)
            .initializeSchema(true)
            .batchingStrategy(TokenCountBatchingStrategy())
            .build()
    }
}