package com.lovelycatv.ai.crystal.node.api.task

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.node.task.ChatTask

/**
 * @author lovelycat
 * @since 2025-03-03 00:17
 * @version 1.0
 */
interface NodeChatTaskBuilder<O: AbstractChatOptions> {
    fun buildChatTask(originalMessageChain: MessageChain): ChatTask<O>

    fun getOptionsClass(): Class<O>
}