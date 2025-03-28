package com.lovelycatv.ai.crystal.openapi.plugin

import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions

/**
 * @author lovelycat
 * @since 2025-03-19 23:17
 * @version 1.0
 */
class ChatOptionsBuilderExtensions private constructor()

inline fun <reified O: AbstractChatOptions> ChatOptionsBuilder(
    platformName: String,
    crossinline builder: (modelName: String) -> O
): ChatOptionsBuilder<O>
    = object : ChatOptionsBuilder<O> {
        override fun build(modelName: String): O = builder.invoke(modelName)
        override fun getPlatformName(): String = platformName
        override fun getOptionsClass(): Class<O> = O::class.java
    }