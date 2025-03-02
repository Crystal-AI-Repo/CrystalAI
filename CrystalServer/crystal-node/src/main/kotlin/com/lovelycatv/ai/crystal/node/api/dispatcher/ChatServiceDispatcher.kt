package com.lovelycatv.ai.crystal.node.api.dispatcher

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.node.data.AbstractChatResult
import com.lovelycatv.ai.crystal.node.service.chat.base.AbstractChatService
import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-03-02 18:13
 * @version 1.0
 */
fun interface ChatServiceDispatcher {
    fun getService(options: KClass<out AbstractChatOptions>): AbstractChatService<AbstractChatOptions, AbstractChatResult, Any>?
}