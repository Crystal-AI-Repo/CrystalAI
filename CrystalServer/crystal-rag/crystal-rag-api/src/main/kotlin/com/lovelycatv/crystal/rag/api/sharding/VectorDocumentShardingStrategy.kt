package com.lovelycatv.crystal.rag.api.sharding

import com.lovelycatv.crystal.rag.api.data.VectorDocument

/**
 * @author lovelycat
 * @since 2025-03-26 16:09
 * @version 1.0
 */
interface VectorDocumentShardingStrategy {
    fun process(originalDocument: VectorDocument): List<VectorDocument>
}