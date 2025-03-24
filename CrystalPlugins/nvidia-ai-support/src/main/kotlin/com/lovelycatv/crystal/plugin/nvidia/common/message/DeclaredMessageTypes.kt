package com.lovelycatv.crystal.plugin.nvidia.common.message

import com.lovelycatv.ai.crystal.common.data.message.IMessageType

/**
 * @author lovelycat
 * @since 2025-03-02 15:44
 * @version 1.0
 */
enum class DeclaredMessageTypes(override val typeName: String) : IMessageType {
    NVIDIA_CHAT_OPTIONS("NVIDIA_CHAT_OPTIONS")
}