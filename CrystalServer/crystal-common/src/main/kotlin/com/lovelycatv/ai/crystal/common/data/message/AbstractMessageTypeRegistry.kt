package com.lovelycatv.ai.crystal.common.data.message

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * @author lovelycat
 * @since 2025-03-02 15:55
 * @version 1.0
 */
interface AbstractMessageTypeRegistry {
    fun registerTypes(objectMapper: ObjectMapper)
}