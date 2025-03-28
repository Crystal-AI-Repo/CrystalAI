package com.lovelycatv.crystal.rag.enums

/**
 * @author lovelycat
 * @since 2025-03-26 15:38
 * @version 1.0
 */
enum class SimilarityFunction(val typeNames: List<String>) {
    COSINE(listOf("cosine", "cos")),
    DOT_PRODUCT(listOf("dot_product", "dot-product", "dotProduct", "dot")),
    L2_NORM(listOf("l2_norm", "l2", "l2norm"));

    companion object {
        fun fromTypeName(typeName: String) = entries.find { function ->
            function.typeNames.map { it.lowercase() }.contains(typeName.lowercase())
        }
    }
}