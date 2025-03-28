package com.lovelycatv.crystal.rag.api.document.impl

import com.lovelycatv.crystal.rag.api.document.VectorDocumentShardingStrategy
import com.lovelycatv.crystal.rag.data.VectorDocument
import java.util.UUID
import kotlin.math.ceil

/**
 * @author lovelycat
 * @since 2025-03-26 16:30
 * @version 1.0
 */
class LengthBasedVectorDocumentShardingStrategy(
    private val maxLengthPerDocument: Int
) : VectorDocumentShardingStrategy {
    override fun process(originalDocument: VectorDocument): List<VectorDocument> {
        val originalContent = originalDocument.content
        val totalLength = originalContent.length
        val shards = ceil((totalLength.toDouble() / maxLengthPerDocument)).toInt()
        val contentList = mutableListOf<String>()

        for (i in 0..<shards) {
            val start = i * maxLengthPerDocument
            val end = if (i == shards - 1) {
                totalLength - 1
            } else {
                (i + 1) * maxLengthPerDocument
            }
            contentList.add(originalContent.slice(start..end))
        }

        return with(originalDocument) {
            val identity = UUID.randomUUID().toString()
            contentList.mapIndexed { index, content ->
                val apart = shards > 1
                this.copy(
                    id = if (apart) UUID.randomUUID().toString() else this.id,
                    apart = apart,
                    metadata = this.metadata.toMutableMap().apply {
                        if (apart) {
                            this[VectorDocument.ORDER] = index
                            this[VectorDocument.IDENTITY] = identity
                        }
                    },
                    content = content
                )
            }
        }
    }
}