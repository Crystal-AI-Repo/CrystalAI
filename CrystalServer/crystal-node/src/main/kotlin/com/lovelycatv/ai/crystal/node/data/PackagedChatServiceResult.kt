package com.lovelycatv.ai.crystal.node.data

/**
 * @author lovelycat
 * @since 2025-02-28 18:39
 * @version 1.0
 */
data class PackagedChatServiceResult<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T?,
    val exception: Exception? = null,
) {
    companion object {
        fun <T> success(data: T?): PackagedChatServiceResult<T> {
            return PackagedChatServiceResult(true, null, data)
        }

        fun <T> success(message: String?, data: T?): PackagedChatServiceResult<T> {
            return PackagedChatServiceResult(true, message, data)
        }

        fun <T> failed(message: String, e: Exception? = null): PackagedChatServiceResult<T> {
            return PackagedChatServiceResult(false, message, null, e)
        }
    }
}