package com.lovelycatv.ai.crystal.node.exception

/**
 * @author lovelycat
 * @since 2025-02-15 16:58
 * @version 1.0
 */
class UnsupportedMessageType(typeName: String) : RuntimeException("Unsupported message type: $typeName")