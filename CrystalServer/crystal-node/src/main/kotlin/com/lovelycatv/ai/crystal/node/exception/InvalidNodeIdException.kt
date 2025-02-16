package com.lovelycatv.ai.crystal.node.exception

/**
 * @author lovelycat
 * @since 2025-02-17 00:28
 * @version 1.0
 */
class InvalidNodeIdException(uuid: String?, msg: String?) : RuntimeException("Invalid node uuid: $uuid, message: $msg")