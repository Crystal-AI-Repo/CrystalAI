package com.lovelycatv.ai.crystal.node.exception

/**
 * @author lovelycat
 * @since 2025-03-01 17:00
 * @version 1.0
 */
class UnsupportedTaskTypeException(msg: String?) : RuntimeException("Unsupported task type, message: $msg")