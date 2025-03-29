package com.lovelycatv.crystal.rag.api.repository.impl

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.BulkResponse
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.search.Hit
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.TransportOptions
import co.elastic.clients.transport.Version
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.crystal.rag.api.embedding.EmbeddingModel
import com.lovelycatv.crystal.rag.api.document.VectorDocumentShardingStrategy
import com.lovelycatv.crystal.rag.api.repository.AbstractVectorRepository
import com.lovelycatv.crystal.rag.data.VectorDocument
import com.lovelycatv.crystal.rag.data.VectorDocumentQuery
import com.lovelycatv.crystal.rag.data.VectorRepositoryOptions
import com.lovelycatv.crystal.rag.enums.SimilarityFunction
import com.lovelycatv.crystal.rag.api.query.ElasticSearchQueryConditionTranslator
import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition
import org.apache.http.HttpHost
import org.apache.http.message.BasicHeader
import org.elasticsearch.client.RestClient
import org.springframework.ai.model.EmbeddingUtils
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.sqrt

/**
 * @author lovelycat
 * @since 2025-03-26 15:23
 * @version 1.0
 */
@OptIn(ExperimentalEncodingApi::class)
class ElasticSearchVectorRepository(
    host: String,
    port: Int,
    ssl: Boolean,
    userName: String,
    password: String,
    private val initSchema: Boolean,
    options: VectorRepositoryOptions,
    embeddingModel: EmbeddingModel,
    documentShardingStrategy: VectorDocumentShardingStrategy
) : AbstractVectorRepository(options, embeddingModel, documentShardingStrategy) {
    private val logger = logger()

    private val elasticsearchClient: ElasticsearchClient

    private val queryConditionTranslator: ElasticSearchQueryConditionTranslator = ElasticSearchQueryConditionTranslator()

    init {
        val httpHost = HttpHost(host, port, if (ssl) "https" else "http")

        val restClient = RestClient.builder(httpHost)
            .setDefaultHeaders(arrayOf(
                BasicHeader("Authorization", "Basic ${java.util.Base64.getEncoder().encodeToString(("$userName:$password").toByteArray())}")
            ))
            .build()

        val version = if (Version.VERSION == null) "Unknown" else Version.VERSION.toString()

        elasticsearchClient = ElasticsearchClient(RestClientTransport(restClient,
            JacksonJsonpMapper(
                ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))))
            .withTransportOptions { t: TransportOptions.Builder -> t.addHeader("user-agent", "crystal-ai elastic-java/$version") }

        this.afterInitialized()
    }

    private fun afterInitialized() {
        if (!this.initSchema) {
            return
        }
        if (!this.isRepositoryExists()) {
            logger.info("Ready to create ElasticSearch index: ${this.getRepositoryName()}")
            createIndexMapping()
        } else {
            logger.info("ElasticSearch index: ${this.getRepositoryName()} is already exists")
        }
    }

    private fun createIndexMapping() {
        elasticsearchClient.indices()
            .create { indexCreator ->
                indexCreator.index(this.getRepositoryName())
                    .mappings { mappingsBuilder ->
                        // Embedding
                        mappingsBuilder.properties("embedding") { propBuilder ->
                            propBuilder.denseVector {
                                it.similarity(getSimilarityFunctionName()).dims(this.options.dimensions)
                            }
                        }
                    }
            }
    }

    private fun getSimilarityFunctionName(): String {
        return when (this.options.similarity) {
            SimilarityFunction.COSINE -> {
                ElasticSearchSimilarityFunction.cosine
            }
            SimilarityFunction.DOT_PRODUCT -> {
                ElasticSearchSimilarityFunction.dot_product
            }
            SimilarityFunction.L2_NORM -> {
                ElasticSearchSimilarityFunction.l2_norm
            }
        }.toString()
    }

    override fun getRepositoryName(): String {
        return this.options.repositoryName
    }

    override fun add(vararg documents: VectorDocument): Boolean {
        require(this.isRepositoryExists()) { "Index ${this.getRepositoryName()} not found" }
        val bulkRequestBuilder = BulkRequest.Builder()

        documents.forEach { doc ->
            val shardingResult = documentShardingStrategy.process(doc)
            val embeddings: List<FloatArray> = embeddingModel.batchEmbed(shardingResult.map { it.content })

            shardingResult.forEachIndexed { index, vectorDocument ->
                bulkRequestBuilder.operations { op ->
                    op.index {
                        val realDocument = ElasticSearchDocument(
                            vectorDocument.id,
                            vectorDocument.content,
                            vectorDocument.apart,
                            vectorDocument.metadata,
                            embeddings[index]
                        )
                        it.index(this.getRepositoryName())
                            .id(vectorDocument.id)
                            .document(realDocument)
                    }
                }
            }

        }

        val bulkRequest: BulkResponse = this.elasticsearchClient.bulk(bulkRequestBuilder.build())

        return if (bulkRequest.errors()) {
            val bulkResponseItems = bulkRequest.items()
            for (bulkResponseItem in bulkResponseItems) {
                check(bulkResponseItem.error() == null) { bulkResponseItem.error()?.reason() ?: "" }
            }
            false
        } else {
            true
        }
    }

    class ElasticSearchDocument(
        val id: String,
        val content: String,
        val apart: Boolean,
        val metadata: Map<String, Any?>,
        val embedding: FloatArray
    )

    override fun remove(vararg documentIds: String): Boolean {
        require(this.isRepositoryExists()) { "Index ${this.getRepositoryName()} not found" }

        val bulkRequestBuilder = BulkRequest.Builder()
        documentIds.forEach {
            bulkRequestBuilder.operations { op ->
                op.delete { idx ->
                    idx.index(this.getRepositoryName()).id(it)
                }
            }
        }

        return this.elasticsearchClient.bulk(bulkRequestBuilder.build()).errors()
    }

    override fun similaritySearch(query: VectorDocumentQuery): List<VectorDocument> {
        require(this.isRepositoryExists()) { "Index ${this.getRepositoryName()} not found" }

        var threshold: Float = query.similarityThreshold
        // reverting l2_norm distance to its original value
        // reverting l2_norm distance to its original value
        if (options.similarity == SimilarityFunction.L2_NORM) {
            threshold = 1 - threshold
        }
        val finalThreshold = threshold
        val vectors: FloatArray = embeddingModel.embed(query.queryContent)

        val res = elasticsearchClient.search(
            { searchRequest ->
                searchRequest.index(this.getRepositoryName())
                    .knn { knn ->
                        knn.queryVector(EmbeddingUtils.toList(vectors))
                            .similarity(finalThreshold)
                            .k(query.topK.toLong())
                            .field("embedding")
                            .numCandidates((1.5 * query.topK).toLong())
                    }
            },
            VectorDocument::class.java)

        return res.hits().hits().map {
            it.toDocument()
        }
    }

    override fun search(queryConditions: List<AbstractQueryCondition>): List<VectorDocument> {
        val response = this.elasticsearchClient.search(
            SearchRequest.of { searchRequestBuilder ->
                searchRequestBuilder.index(this.getRepositoryName()).apply {
                    queryConditionTranslator.translate(queryConditions, this)
                }
            },
            VectorDocument::class.java
        )

        return response.hits().hits().map { it.toDocument() }
    }

    override fun isRepositoryExists(): Boolean {
        return elasticsearchClient.indices()
            .exists { it.index(this.getRepositoryName()) }
            .value()
    }

    private fun Hit<VectorDocument>.toDocument(): VectorDocument {
        val document = this.source()!!
        val score = this.score() ?: 0.0
        return document.copy(
            metadata = document.metadata.toMutableMap().apply {
                this[VectorDocument.DISTANCE] = 1 - normalizeSimilarityScore(score)
            },
            score = normalizeSimilarityScore(score)
        )
    }

    // more info on score/distance calculation
    // https://www.elastic.co/guide/en/elasticsearch/reference/current/knn-search.html#knn-similarity-search
    private fun normalizeSimilarityScore(score: Double): Double {
        return when (this.options.similarity) {
            SimilarityFunction.L2_NORM ->
                // the returned value of l2_norm is the opposite of the other functions
                // (closest to zero means more accurate), so to make it consistent
                // with the other functions the reverse is returned applying a "1-"
                // to the standard transformation
                1 - sqrt(1 / score - 1)
            else -> 2 * score - 1
        }
    }
}