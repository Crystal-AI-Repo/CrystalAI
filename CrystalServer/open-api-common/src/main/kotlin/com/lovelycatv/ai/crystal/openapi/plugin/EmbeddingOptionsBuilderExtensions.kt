package com.lovelycatv.ai.crystal.openapi.plugin

import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions

/**
 * @author lovelycat
 * @since 2025-03-28 17:18
 * @version 1.0
 */
class EmbeddingOptionsBuilderExtensions private constructor()

inline fun <reified O: AbstractEmbeddingOptions> EmbeddingOptionsBuilder(
    platformName: String,
    crossinline builder: (modelName: String) -> O
): EmbeddingOptionsBuilder<O>
    = object : EmbeddingOptionsBuilder<O> {
    override fun build(modelName: String): O = builder.invoke(modelName)
    override fun getPlatformName(): String = platformName
    override fun getOptionsClass(): Class<O> = O::class.java
}