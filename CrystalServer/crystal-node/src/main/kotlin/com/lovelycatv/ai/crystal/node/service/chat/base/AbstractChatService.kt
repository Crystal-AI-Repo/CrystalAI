package com.lovelycatv.ai.crystal.node.service.chat.base

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.node.data.AbstractChatResult
import com.lovelycatv.ai.crystal.node.data.PackagedChatServiceResult
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author lovelycat
 * @since 2025-02-28 14:23
 * @version 1.0
 */
abstract class AbstractChatService<OPTIONS: AbstractChatOptions, BLOCKING: AbstractChatResult, STREAMING> {
    private val streamCoroutineScope = CoroutineScope(Dispatchers.IO + CoroutineName("StreamGenerate"))

    protected abstract suspend fun generate(
        content: List<PromptMessage>,
        options: OPTIONS?,
        stream: Boolean
    ): PackagedChatServiceResult<Any?>

    @Suppress("UNCHECKED_CAST")
    fun streamGenerate(
        content: List<PromptMessage>,
        options: OPTIONS?,
        onNewTokenReceived: ChatStreamCallback? = null,
        onFailed: ChatStreamRequestFailedCallback? = null,
        onCompleted: ChatStreamCompletedCallback
    ) {
        streamCoroutineScope.launch {
            val result = this@AbstractChatService.generate(content, options, true) as PackagedChatServiceResult<STREAMING?>
            if (result.success) {
                this@AbstractChatService.streamGenerateImpl(result.data as STREAMING, onNewTokenReceived, onCompleted)
            } else {
                onFailed?.invoke(result)
            }
        }
    }

    protected abstract fun streamGenerateImpl(
        stream: STREAMING,
        onNewTokenReceived: ChatStreamCallback? = null,
        onCompleted: ChatStreamCompletedCallback
    )

    @Suppress("UNCHECKED_CAST")
    suspend fun blockingGenerate(content: List<PromptMessage>, options: OPTIONS?): PackagedChatServiceResult<out BLOCKING?> {
        val result = this.generate(content, options, false) as PackagedChatServiceResult<BLOCKING?>
        return if (result.success) {
            this.blockingGenerateImpl(result.data as BLOCKING)
        } else {
            result
        }
    }

    protected open suspend fun blockingGenerateImpl(blockingResult: BLOCKING): PackagedChatServiceResult<BLOCKING> {
        return PackagedChatServiceResult.success("", blockingResult)
    }
}