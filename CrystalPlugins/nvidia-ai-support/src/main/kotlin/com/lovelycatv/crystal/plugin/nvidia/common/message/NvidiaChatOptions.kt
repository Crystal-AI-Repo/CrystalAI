package com.lovelycatv.crystal.plugin.nvidia.common.message

import com.fasterxml.jackson.annotation.JsonTypeName
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions

/**
 * @author lovelycat
 * @since 2025-03-02 15:30
 * @version 1.0
 */
@JsonTypeName("NVIDIA_CHAT_OPTIONS")
class NvidiaChatOptions(
    modelName: String?
) : AbstractChatOptions(modelName, DeclaredMessageTypes.NVIDIA_CHAT_OPTIONS)