package com.lovelycatv.ai.crystal.common.vo

import com.lovelycatv.ai.crystal.common.data.message.MessageChain

/**
 * @author lovelycat
 * @since 2025-02-28 19:56
 * @version 1.0
 */
data class NodeTaskVO(
    val sessionId: String,
    val taskType: String,
    val taskClass: String,
    val expireTime: Long,
    val priority: Int,
    val originalMessages: MessageChain
)