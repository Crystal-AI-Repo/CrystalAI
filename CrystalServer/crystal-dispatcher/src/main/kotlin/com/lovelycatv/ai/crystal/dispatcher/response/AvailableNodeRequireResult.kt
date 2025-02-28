package com.lovelycatv.ai.crystal.dispatcher.response

import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode

/**
 * @author lovelycat
 * @since 2025-02-28 23:20
 * @version 1.0
 */
data class AvailableNodeRequireResult(
    val success: Boolean,
    val message: String? = null,
    val node: RegisteredNode? = null
) {
    companion object {
        fun success(node: RegisteredNode): AvailableNodeRequireResult {
            return AvailableNodeRequireResult(success = true, message = null, node = node)
        }

        fun success(message: String, node: RegisteredNode): AvailableNodeRequireResult {
            return AvailableNodeRequireResult(success = true, message = message, node = node)
        }

        fun failed(message: String): AvailableNodeRequireResult {
            return AvailableNodeRequireResult(success = false, message = message, node = null)
        }
    }
}