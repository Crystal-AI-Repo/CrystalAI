package com.lovelycatv.ai.crystal.openapi.plugin

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions

/**
 * @author lovelycat
 * @since 2025-03-19 23:06
 * @version 1.0
 */
interface ChatOptionsBuilder<O: AbstractChatOptions> {
    fun build(modelName: String): O

    fun getPlatformName(): String

    fun getOptionsClass(): Class<O>
}