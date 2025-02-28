package com.lovelycatv.ai.crystal.node.service.chat.base

import com.lovelycatv.ai.crystal.node.data.PackagedChatServiceResult

/**
 * @author lovelycat
 * @since 2025-02-28 14:52
 * @version 1.0
 */
typealias ChatStreamCallback = (String) -> Unit
typealias ChatStreamCompletedCallback = (received: List<String>, generatedTokens: Long, totalTokens: Long) -> Unit
typealias ChatStreamRequestFailedCallback = (failedResult: PackagedChatServiceResult<*>) -> Unit