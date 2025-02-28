package com.lovelycatv.ai.crystal.node.data

/**
 * @author lovelycat
 * @since 2025-02-28 19:51
 * @version 1.0
 */
abstract class AbstractTask(
    val taskType: Type
) {
    enum class Type {
        CHAT
    }
}