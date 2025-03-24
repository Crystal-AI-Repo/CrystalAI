package com.lovelycatv.crystal.plugin.nvidia.common.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.lovelycatv.crystal.plugin.nvidia.common.message.NvidiaChatOptions

@JsonSubTypes(
    JsonSubTypes.Type(value = NvidiaChatOptions::class, name = "NVIDIA_CHAT_OPTIONS")
)
abstract class AbstractMessageMixIn