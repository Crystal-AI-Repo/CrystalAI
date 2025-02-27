package com.lovelycatv.ai.crystal.dispatcher.data.node

import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatResponseMessage

/**
 * @author lovelycat
 * @since 2025-02-27 00:34
 * @version 1.0
 */
data class OneTimeChatRequestResult(
    val isRequestSent: Boolean,
    val isSuccess: Boolean,
    val message: String = "",
    val results: List<OllamaChatResponseMessage> = listOf()
)